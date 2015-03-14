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
package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS

@Configuration
@EnableWebSecurity @Order(0)
class TokenEndpointSecurityConfig extends WebSecurityConfigurerAdapter implements ConfigurationSupport {

    @Autowired @Qualifier('client')
    AuthenticationManager clientAuthManager


    TokenEndpointSecurityConfig() {
        super(true) // disable defaults
    }

    AuthenticationManager authenticationManager() { clientAuthManager }

    void configure(HttpSecurity http) {
        http.requestMatchers()
                .antMatchers( p('oaas.endpoint.token'), p('oaas.endpoint.check_token.uri') )
                .and()
            .exceptionHandling()
                .authenticationEntryPoint( oauthBasicAuthenticationEntryPoint() )
                .accessDeniedHandler( new OAuth2AccessDeniedHandler() )
                .and()
            .headers()
                .cacheControl()
                .and()
            .sessionManagement()
                .sessionCreationPolicy( STATELESS )
                .and()
            .servletApi()
                .and()
            .anonymous()
                .and()
            .httpBasic()
                .authenticationEntryPoint( oauthBasicAuthenticationEntryPoint() )
                .and()
            .authorizeRequests()
                .antMatchers( p('oaas.endpoint.token') )
                    .fullyAuthenticated()
                .antMatchers( p('oaas.endpoint.check_token.uri') )
                    .access( p('oaas.endpoint.check_token.access') )

        if ( p('auth.client.auth_scheme.form.allow') as boolean ) {
            http.addFilterAfter( clientFormAuthenticationFilter(), BasicAuthenticationFilter )
        }
    }

    /**
     * Client authentication with HTTP Basic scheme. This is the recommended way by specification.
     */
    @Bean oauthBasicAuthenticationEntryPoint() {
        new OAuth2AuthenticationEntryPoint (
            realmName: 'Zuul OAAS'
        )
    }

    /**
     * Filter that supports client authentication with credentials in request body (parameters
     * client_id and client_secret). Note: This method of authentication is not recommended by
     * OAuth specification (draft-ietf-oauth-v2-31, 16)! Clients should use HTTP Basic scheme
     * instead.
     */
    @Bean @Lazy clientFormAuthenticationFilter() {
        new ClientCredentialsTokenEndpointFilter( p('oaas.endpoint.token') ).with {
            it.authenticationManager = authenticationManager(); it
        }
    }
}
