package cz.cvut.zuul.oaas.test.factories

import cz.cvut.oauth.provider.spring.TokenInfo
import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.models.Scope
import cz.cvut.zuul.oaas.models.User
import cz.cvut.zuul.oaas.models.enums.AuthorizationGrant
import cz.cvut.zuul.oaas.models.enums.Visibility
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.common.*
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.test.factories.CustomGeneratorSamples.anyEmail
import static net.java.quickcheck.generator.CombinedGeneratorSamples.anyMap
import static net.java.quickcheck.generator.CombinedGeneratorSamples.anySet
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.*
import static net.java.quickcheck.generator.PrimitiveGenerators.*

/**
 * Factory for building testing (domain) objects.
 *
 * TODO refactor me!
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
     * @param clazz The class under which will be registered.
     * @param closure The closure that builds instance of the class.
     */
    def <T> void registerBuilder(Class<T> clazz, Closure<? extends T> closure) {
        builders[clazz] = closure
    }

    /**
     * Registers superclass key for the already registered builder.
     *
     * @param superClass The superclass to register.
     * @param registeredClass The class that has already registered builder.
     */
    def <T> void registerSuperclass(Class<T> superClass, Class<? extends T> registeredClass) {
        builders[superClass] = builders[registeredClass]
    }

    /**
     * Builds instance of the given class, populated with random values.
     *
     * @param clazz The type of object to build.
     * @param values The values to pass to the builder.
     * @return
     */
    def <T> T build(Class<T> clazz, Map<String, Object> values = [:]) {
        if (builders[clazz]) {
            builders[clazz].call(values)
        } else {
            ObjectFeeder.build(clazz, values)
        }
    }

    /**
     * Builds list of instances of the given class, populated with random values.
     *
     * @param clazz The type of object to build.
     * @param minSize The minimal size of the list to generate (default is 1).
     * @param maxSize The maximal size of the list to generate (default is 3).
     * @param values The values to pass to the builder (optional).
     * @return
     */
    def <T> List<T> buildListOf(Class<T> clazz, int minSize = 1, int maxSize = 3, Map<String, Object> values = [:]) {
        new T[ anyInteger(minSize, maxSize) ].collect {
            build(clazz, values)
        }
    }

    /**
     * @see #buildListOf(Class, int, int, Map)
     */
    def <T> List<T> buildListOf(Class<T> clazz, Map<String, Object> values) {
        buildListOf(clazz, 1, 3, values)
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

            def object = new DefaultAuthorizationRequest(authzParams).with {
                authorities = buildListOf(GrantedAuthority)
                scope = anySet(letterStrings(5, 10), integers(1, 3))
                return it
            }
            ObjectFeeder.populate(object)
        }

        registerBuilder(UsernamePasswordAuthenticationToken) { values ->
            def user = build(User, values)
            new UsernamePasswordAuthenticationToken(user, null, user.authorities)
        }

        registerBuilder(User) { values ->
            String username = values['username'] ?: anyLetterString(5, 10)
            new User(username, anyEmail(), anyLetterString(), anyLetterString(), buildListOf(GrantedAuthority))
        }

        registerBuilder(SimpleGrantedAuthority) {
            new SimpleGrantedAuthority(
                    anyFixedValue('ROLE_USER', 'ROLE_CLIENT', 'ROLE_ADMIN', 'ROLE_MASTER')
            )
        }


        registerBuilder(Client) { values ->
            def client = new Client(
                clientId: anyLetterString(5, 10),
                authorizedGrantTypes: anySet(enumValues(AuthorizationGrant), 1, 2),
                authorities: buildListOf(GrantedAuthority, 0, 3)
            )
            ObjectFeeder.populate(client)
            values.each { prop, value ->
                client[prop] = value
            }
            return client
        }

        registerBuilder(ResourceDTO) { values ->
            def resource = new ResourceDTO(
                    resourceId: anyLetterString(5, 10),
                    baseUrl: 'http://example.org',
                    description: anyLetterString(0, 255),
                    name: anyLetterString(5, 255),
                    version: anyLetterString(0, 255),
                    visibility: anyEnumValue(Visibility),
                    auth: new ResourceDTO.Auth(
                            scopes: buildListOf(ResourceDTO.Scope, 0, 3)
                    )
            )
            values.each { prop, value ->
                resource[prop] = value
            }
            return resource
        }

        registerBuilder(Resource) { values ->
            def resource = new Resource(
                    scopes: buildListOf(Scope)
            )
            values.each { prop, value ->
                resource[prop] = value
            }
            return resource
        }

        registerBuilder(TokenInfo) { values ->
            def token = new TokenInfo(
                    clientAuthorities: buildListOf(GrantedAuthority),
                    userAuthorities: buildListOf(GrantedAuthority)
            )
            ObjectFeeder.populate(token)
            values.each { prop, value ->
                token[prop] = value
            }
            return token
        }


        //////// Superclasses ////////

        registerSuperclass OAuth2AccessToken, DefaultOAuth2AccessToken
        registerSuperclass OAuth2RefreshToken, DefaultOAuth2RefreshToken
        registerSuperclass ExpiringOAuth2RefreshToken, DefaultExpiringOAuth2RefreshToken
        registerSuperclass AuthorizationRequest, DefaultAuthorizationRequest
        registerSuperclass GrantedAuthority, SimpleGrantedAuthority
        registerSuperclass Authentication, UsernamePasswordAuthenticationToken
    }
}
