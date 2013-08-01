package cz.cvut.zuul.oaas.test.factories

import cz.cvut.zuul.oaas.models.Auth
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.models.ExtendedUserDetails
import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.models.Scope
import cz.cvut.zuul.oaas.models.enums.AuthorizationGrant
import cz.cvut.zuul.oaas.models.enums.Visibility
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication

import static net.java.quickcheck.generator.CombinedGeneratorSamples.anyList
import static net.java.quickcheck.generator.CombinedGeneratorSamples.anyMap
import static net.java.quickcheck.generator.CombinedGeneratorSamples.anySet
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.*
import static net.java.quickcheck.generator.PrimitiveGenerators.enumValues
import static net.java.quickcheck.generator.PrimitiveGenerators.integers
import static net.java.quickcheck.generator.PrimitiveGenerators.letterStrings

/**
 * Factory for building testing (domain) objects.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ObjectFactory {

    private Map<Class, Closure> builders = [:]

    ObjectFactory() {
        registerAll()
    }

    /**
     * Registers builder for the given class.
     *
     * @param clazz the class under which will be registered
     * @param closure the closure that builds instance of the class
     */
    def <T> void registerBuilder(Class<T> clazz, Closure<? extends T> closure) {
        builders[clazz] = closure
    }

    def <T> void registerAlias(Class<T> alias, Class<? extends T> existing) {
        builders[alias] = builders[existing]
    }

    /**
     * Builds instance of the given class, populated with random values.
     *
     * @param clazz the type of object to build
     * @param values the values to pass to the builder
     * @return
     */
    def <T> T build(Class<T> clazz, Map<String, Object> values = [:]) {
        if (builders[clazz]) {
            builders[clazz].call(values)
        } else {
            ObjectFeeder.build(clazz, values)
        }
    }


    protected void registerAll() {

        //////// Tokens ////////

        registerBuilder(DefaultOAuth2AccessToken) { values ->
            def value = values['value'] ?: anyLetterString(5, 10)

            def object = new DefaultOAuth2AccessToken(value).with {
                scope = values['scope'] ?: anySet(letterStrings(5, 10))
                refreshToken = values['refreshToken']
                additionalInformation = anyMap(letterStrings(5, 10), letterStrings(5, 10))
                return it
            }
            ObjectFeeder.populate(object)
        }

        registerBuilder(DefaultOAuth2RefreshToken) {
            new DefaultOAuth2RefreshToken(anyLetterString(5, 10))
        }

        registerBuilder(DefaultExpiringOAuth2RefreshToken) {
            new DefaultExpiringOAuth2RefreshToken(anyLetterString(5, 10), anyDate())
        }


        //////// Authentication ////////

        registerBuilder(OAuth2Authentication) { values ->
            def userAuth = values['clientOnly'] ? null : build(Authentication, values)

            new OAuth2Authentication(build(AuthorizationRequest, values), userAuth)
        }

        registerBuilder(DefaultAuthorizationRequest) { values ->
            def authzParams = [:].with {
                put(AuthorizationRequest.CLIENT_ID, values['clientId'] ?: anyLetterString(5, 10))
                anyBoolean() && put(AuthorizationRequest.STATE, anyLetterString(5, 10))
                anyBoolean() && put(AuthorizationRequest.REDIRECT_URI, anyLetterString(5, 10))
                return it
            } as Map

            def roles = new GrantedAuthority[anyInteger(1, 3)].collect {
                build(GrantedAuthority)
            }

            def object = new DefaultAuthorizationRequest(authzParams).with {
                authorities = roles
                scope = anySet(letterStrings(5, 10), integers(1, 3))
                return it
            }
            ObjectFeeder.populate(object)
        }

        registerBuilder(UsernamePasswordAuthenticationToken) { values ->
            def user = build(ExtendedUserDetails, values)
            new UsernamePasswordAuthenticationToken(user, null, user.authorities)
        }

        registerBuilder(ExtendedUserDetails) { values ->
            def roles = new GrantedAuthority[anyInteger(1, 3)].collect {
                build(GrantedAuthority)
            }
            String username = values['username'] ?: anyLetterString(5, 10)

            new ExtendedUserDetails(
                    cz.cvut.zuul.oaas.test.factories.CustomGeneratorSamples.anyEmail(), anyLetterString(), anyLetterString(), username, 'empty', roles
            )
        }

        registerBuilder(SimpleGrantedAuthority) {
            new SimpleGrantedAuthority(
                    anyFixedValue('ROLE_USER', 'ROLE_CLIENT', 'ROLE_ADMIN', 'ROLE_MASTER')
            )
        }


        registerBuilder(Client) { values ->
            def roles = new GrantedAuthority[anyInteger(0, 3)].collect {
                build(GrantedAuthority)
            }
            def client = new Client(
                clientId: anyLetterString(5, 10),
                authorizedGrantTypes: anySet(enumValues(AuthorizationGrant), 1, 2),
                authorities: roles
            )
            ObjectFeeder.populate(client)
            values.each { prop, value ->
                client[prop] = value
            }
            return client
        }

        registerBuilder(Resource) { values ->
            def scopes = new Scope[anyInteger(0, 3)].collect {
                build(Scope)
            }
            def resource = new Resource(
                    id: anyLetterString(5, 10),
                    baseUrl: 'http://example.org',
                    description: anyLetterString(0, 255),
                    name: anyLetterString(5, 255),
                    version: anyLetterString(0, 255),
                    title: anyLetterString(0, 255),
                    visibility: anyEnumValue(Visibility).toString(),
                    auth: new Auth(
                            scopes: scopes
                    )
            )
            values.each { prop, value ->
                resource[prop] = value
            }
            return resource
        }


        //////// Aliases ////////

        registerAlias(OAuth2AccessToken, DefaultOAuth2AccessToken)
        registerAlias(OAuth2RefreshToken, DefaultOAuth2RefreshToken)
        registerAlias(ExpiringOAuth2RefreshToken, DefaultExpiringOAuth2RefreshToken)
        registerAlias(AuthorizationRequest, DefaultAuthorizationRequest)
        registerAlias(GrantedAuthority, SimpleGrantedAuthority)
        registerAlias(Authentication, UsernamePasswordAuthenticationToken)
    }
}
