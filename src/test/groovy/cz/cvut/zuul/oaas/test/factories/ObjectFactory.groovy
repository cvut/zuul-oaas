package cz.cvut.zuul.oaas.test.factories

import cz.cvut.zuul.oaas.models.ExtendedUserDetails
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication

import static net.java.quickcheck.generator.CombinedGeneratorSamples.anyMap
import static net.java.quickcheck.generator.CombinedGeneratorSamples.anySet
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.*
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


        //////// Aliases ////////

        registerAlias(OAuth2AccessToken, DefaultOAuth2AccessToken)
        registerAlias(AuthorizationRequest, DefaultAuthorizationRequest)
        registerAlias(GrantedAuthority, SimpleGrantedAuthority)
        registerAlias(Authentication, UsernamePasswordAuthenticationToken)
    }
}
