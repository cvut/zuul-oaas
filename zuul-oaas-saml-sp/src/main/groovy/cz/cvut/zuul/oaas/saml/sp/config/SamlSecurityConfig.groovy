/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.saml.sp.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.saml.sp.SamlAttributesUserDetailsService
import cz.cvut.zuul.oaas.saml.sp.metadata.ContactPersonBuilder
import cz.cvut.zuul.oaas.saml.sp.metadata.EnhanceableMetadataGenerator
import cz.cvut.zuul.oaas.saml.sp.metadata.MetadataDisplayFilter
import cz.cvut.zuul.oaas.saml.sp.metadata.OrganizationBuilder
import cz.cvut.zuul.oaas.saml.sp.support.OpenSSLKeyStoreBuilder
import groovy.transform.Memoized
import org.apache.commons.httpclient.HttpClient
import org.opensaml.saml2.metadata.EntityDescriptor
import org.opensaml.saml2.metadata.provider.EntityRoleFilter
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider
import org.opensaml.saml2.metadata.provider.MetadataFilterChain
import org.opensaml.xml.parse.StaticBasicParserPool
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.saml.SAMLAuthenticationProvider
import org.springframework.security.saml.SAMLBootstrap
import org.springframework.security.saml.SAMLEntryPoint
import org.springframework.security.saml.SAMLProcessingFilter
import org.springframework.security.saml.context.SAMLContextProviderImpl
import org.springframework.security.saml.key.JKSKeyManager
import org.springframework.security.saml.log.SAMLDefaultLogger
import org.springframework.security.saml.metadata.CachingMetadataManager
import org.springframework.security.saml.metadata.ExtendedMetadata
import org.springframework.security.saml.metadata.MetadataGeneratorFilter
import org.springframework.security.saml.parser.ParserPoolHolder
import org.springframework.security.saml.processor.HTTPPostBinding
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding
import org.springframework.security.saml.processor.SAMLProcessorImpl
import org.springframework.security.saml.util.VelocityFactory
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl
import org.springframework.security.saml.websso.WebSSOProfileImpl
import org.springframework.security.saml.websso.WebSSOProfileOptions
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.access.channel.ChannelProcessingFilter
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

import javax.xml.namespace.QName

import static org.opensaml.common.xml.SAMLConstants.SAML20MD_NS

// TODO add logout filters
@Configuration
@Profile('saml')
@EnableWebSecurity @Order(10)
class SamlSecurityConfig extends WebSecurityConfigurerAdapter implements ConfigurationSupport {

    final velocityEngine = VelocityFactory.getEngine()

    @Value('${auth.user.saml.sp.metadata.generate}') boolean generateSpMetadata


    void configure(HttpSecurity http) {
        http.requestMatchers()
            .antMatchers(
                    p('auth.user.saml.endpoint.login'),
                    p('auth.user.saml.endpoint.websso') + '/**',
                    p('auth.user.saml.endpoint.metadata')
                ).and()
            .csrf()
               .disable()
            .httpBasic()
                .authenticationEntryPoint( samlEntryPoint() )
                .and()
            .addFilterAfter( samlFilterChain(), BasicAuthenticationFilter)
            .authorizeRequests()
                .anyRequest().permitAll()

        if (generateSpMetadata) {
            http.addFilterBefore( samlMetadataGeneratorFilter(), ChannelProcessingFilter )
        }
    }

    void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider( samlAuthenticationProvider() )
    }


    /**
     * Initialize OpenSAML library.
     */
    @Bean static BeanFactoryPostProcessor samlBootstrap() {
        new SAMLBootstrap()
    }

    /**
     * Returns the authentication manager currently used by Spring.
     * It represents a bean definition with the aim allow wiring from other classes performing
     * the Inversion of Control (IoC).
     */
    @Bean @Qualifier('saml')
    AuthenticationManager authenticationManagerBean() {
        super.authenticationManagerBean()
    }

    /**
     * SAML Authentication Provider responsible for validating of received SAML messages.
     */
    @Bean samlAuthenticationProvider() {
        new SAMLAuthenticationProvider (
            userDetails: samlUserDetailsService(),
            samlLogger: samlLogger(),
            consumer: webSSOprofileConsumer(),
            hokConsumer: webSSOprofileConsumer(),  // workaround for https://jira.spring.io/browse/SES-169
            forcePrincipalAsString: false
        )
    }

    /**
     * Service that maps SAML assertion to an {@link cz.cvut.zuul.oaas.models.User} object.
     */
    @Bean samlUserDetailsService() {
        new SamlAttributesUserDetailsService (
            emailAttrNames:     p('auth.user.saml.attribute.email').split(/,\s*/),
            firstNameAttrNames: p('auth.user.saml.attribute.first_name').split(/,\s*/),
            lastNameAttrNames:  p('auth.user.saml.attribute.last_name').split(/,\s*/),
            usernameAttrNames:  p('auth.user.saml.attribute.username').split(/,\s*/)
        )
    }


    //// Metadata ////

    @Bean(destroyMethod = 'destroy') samlMetadataManager() {
        new CachingMetadataManager([ samlIdpMetadataProvider() ]).with {
            it.keyManager = samlKeyManager(); it
        }
    }

    @Bean(initMethod = 'initialize', destroyMethod = 'destroy')
    def samlIdpMetadataProvider() {
        new HTTPMetadataProvider (
            samlIdpMetadataTimer(),
            new HttpClient(),
            p('auth.user.saml.idp.metadata.url')
        ).with {
            parserPool = samlXmlParserPool()
            requireValidMetadata = true
            metadataFilter = samlIdPMetadataFilter()
            minRefreshDelay = ( p('auth.user.saml.idp.metadata.min_refresh_delay') as int ) * 100
            maxRefreshDelay = ( p('auth.user.saml.idp.metadata.max_refresh_delay') as int ) * 100
            it
        }
    }

    /**
     * Timer for refreshing IDP metadata.
     */
    @Bean(destroyMethod = 'cancel') samlIdpMetadataTimer() {
        new Timer('IDP-metadata-reload', true)
    }

    /**
     * Metadata filter chain that removes all SP entity descriptors and keeps only IdP(s).
     * Note: It must be wrapped in FilterChain, otherwise MetadataManager fails.
     */
    @Bean samlIdPMetadataFilter() {
        new MetadataFilterChain(filters: [
            new EntityRoleFilter([
                new QName(SAML20MD_NS, 'IDPSSODescriptor'),
                new QName(SAML20MD_NS, 'AttributeAuthorityDescriptor')
            ])
        ])
    }

    /**
     * Generator of SP metadata describing the application in the current deployment environment.
     */
    @Bean @Lazy samlSpMetadataGenerator() {
        new EnhanceableMetadataGenerator (
            entityId: p('auth.user.saml.sp.metadata.entity_id'),
            entityBaseURL: p('auth.user.saml.sp.metadata.entity_base_url') ?: null,
            extendedMetadata: new ExtendedMetadata(signMetadata: true, local: true),
            samlWebSSOFilter: samlWebSSOProcessingFilter(),
            samlEntryPoint: samlEntryPoint(),
            keyManager: samlKeyManager(),

            metadataEnhancer: { EntityDescriptor desc ->
                desc.contactPersons.addAll( contactPeople )
                desc.organization = organization
            }
        )
    }


    //// Filters ////

    /**
     * Defines the security filter chain in order to support SSO Auth by using SAML 2.0.
     */
    @Bean samlFilterChain() {
        def filters = [
            samlEntryPoint(),
            samlWebSSOProcessingFilter()
        ]
        if (generateSpMetadata) {
            filters << samlMetadataDisplayFilter()
        }
        new FilterChainProxy(filters.collect { filter ->
            new DefaultSecurityFilterChain(new AntPathRequestMatcher(filter.filterProcessesUrl), filter)
        })
    }

    /**
     * Entry point to initialize authentication.
     */
    @Bean samlEntryPoint() {
        new SAMLEntryPoint (
            filterProcessesUrl: p('auth.user.saml.endpoint.login'),
            webSSOprofile: webSSOprofile(),
            samlLogger: samlLogger(),
            contextProvider: samlContextProvider(),
            metadata: samlMetadataManager(),
            defaultProfileOptions: new WebSSOProfileOptions(
                includeScoping: false
        ))
    }

    /**
     * Filter that automatically generates default SP metadata.
     */
    @Bean @Lazy samlMetadataGeneratorFilter() {
        new MetadataGeneratorFilter( samlSpMetadataGenerator() ).with {
            it.displayFilter = samlMetadataDisplayFilter()
            it.manager = samlMetadataManager(); it
        }
    }

    /**
     * The filter is waiting for connections on URL suffixed with filterSuffix and presents
     * SP metadata there.
     */
    @Bean @Lazy samlMetadataDisplayFilter() {
        new MetadataDisplayFilter (
            filterProcessesUrl: p('auth.user.saml.endpoint.metadata'),
            manager: samlMetadataManager(),
            contextProvider: samlContextProvider(),
            keyManager: samlKeyManager()
        )
    }

    /**
     * Processing filter for WebSSO profile messages.
     */
    @Bean samlWebSSOProcessingFilter() {
        new SAMLProcessingFilter (
            filterProcessesUrl: p('auth.user.saml.endpoint.websso'),
            SAMLProcessor: samlProcessor(),
            contextProvider: samlContextProvider(),
            authenticationManager: authenticationManagerBean(),
            authenticationSuccessHandler: successRedirectHandler()
            //authenticationFailureHandler: authenticationFailureHandler()
        )
    }

    /**
     * Handler deciding where to redirect user after successful login.
     */
    @Bean successRedirectHandler() {
        new SavedRequestAwareAuthenticationSuccessHandler(defaultTargetUrl: '/')
    }

    /**
     * Processor for parsing SAML messages.
     */
    @Bean samlProcessor() {
        new SAMLProcessorImpl([
            new HTTPRedirectDeflateBinding( samlXmlParserPool() ),
            new HTTPPostBinding( samlXmlParserPool(), velocityEngine )
        ])
    }


    //// SAML Profiles ////

    /**
     * SAML 2.0 WebSSO Assertion Consumer.
     *
     * XXX: Alias hokWebSSOprofileConsumer is a workaround for https://jira.spring.io/browse/SES-169.
     */
    @Bean(name = ['webSSOprofileConsumer', 'hokWebSSOprofileConsumer'])
    def webSSOprofileConsumer() {
        new WebSSOProfileConsumerImpl (
            metadata: samlMetadataManager(),
            processor: samlProcessor()
        )
    }

    /**
     * SAML 2.0 Web SSO profile.
     */
    @Bean webSSOprofile() {
        new WebSSOProfileImpl (
            metadata: samlMetadataManager(),
            processor: samlProcessor()
        )
    }


    //// Support ////

    /**
     * XML parser pool needed for OpenSAML parsing.
     */
    @Bean(initMethod = 'initialize') samlXmlParserPool() {
        def pool = new StaticBasicParserPool()
        ParserPoolHolder.pool = pool
        pool
    }

    /**
     * Logger for SAML messages and events.
     */
    @Bean samlLogger() {
        new SAMLDefaultLogger()
    }

    /**
     * Provider of default SAML Context.
     */
    @Bean samlContextProvider() {
        new SAMLContextProviderImpl(
            metadata: samlMetadataManager(),
            keyManager: samlKeyManager()
        )
    }

    /**
     * Central storage of cryptographic keys.
     */
    @Bean samlKeyManager() {
        def keyFile = resource( p('auth.user.saml.sp.credentials.key_file') )
        def certFile = resource( p('auth.user.saml.sp.credentials.cert_file') )

        def keyStore = new OpenSSLKeyStoreBuilder()
            .addKey('default', keyFile, certFile)
            .keyStore

        new JKSKeyManager(keyStore, [default: OpenSSLKeyStoreBuilder.KEY_PASSWORD], 'default')
    }


    @Memoized
    def getContactPeople() {
        subPropertiesList('auth.user.saml.sp.metadata.contact_person').collect { props ->
            new ContactPersonBuilder(props).build()
        }
    }

    @Memoized
    def getOrganization() {
        if (subProperties("auth.user.saml.sp.metadata.organization.").empty) {
            return null
        }
        new OrganizationBuilder (
            names: subProperties("auth.user.saml.sp.metadata.organization.name."),
            displayNames: subProperties("auth.user.saml.sp.metadata.organization.display_name."),
            URLs: subProperties("auth.user.saml.sp.metadata.organization.url.")
        ).build()
    }
}
