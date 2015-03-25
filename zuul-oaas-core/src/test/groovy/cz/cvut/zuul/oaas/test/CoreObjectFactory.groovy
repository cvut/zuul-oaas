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
package cz.cvut.zuul.oaas.test

import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.models.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.common.*
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request

import static cz.cvut.zuul.oaas.test.CustomGeneratorSamples.anyEmail
import static net.java.quickcheck.generator.CombinedGeneratorSamples.anyMap
import static net.java.quickcheck.generator.CombinedGeneratorSamples.anySet
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.*
import static net.java.quickcheck.generator.PrimitiveGenerators.*

/**
 * Factory for building testing (domain) objects.
 *
 * TODO refactor me!
 */
class CoreObjectFactory extends ObjectFactory {

    CoreObjectFactory() {

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

        registerBuilder(PersistableAccessToken) {
            new PersistableAccessToken(build(OAuth2AccessToken), build(OAuth2Authentication))
        }


        //////// Authentication ////////

        registerBuilder(OAuth2Authentication) { values ->
            def userAuth = values['clientOnly'] ? null : build(Authentication, values)

            new OAuth2Authentication(build(OAuth2Request, values), userAuth)
        }

        registerBuilder(OAuth2Request) { values ->
            new OAuth2Request(
                    [:],
                    values['clientId'] as String ?: anyLetterString(5, 10),
                    buildListOf(GrantedAuthority),
                    anyBoolean(),
                    anySet(letterStrings(5, 10), integers(1, 3)),
                    anySet(letterStrings(5, 10), integers(1, 3)),
                    anyLetterString(5, 10),
                    anySet(letterStrings(5, 10)),
                    [:]
            )
        }

        registerBuilder(UsernamePasswordAuthenticationToken) { values ->
            def user = build(User, values)
            new UsernamePasswordAuthenticationToken(user, null, user.authorities)
        }

        registerBuilder(User) { values ->
            def username = values['username'] ?: anyLetterString(5, 10)
            def authorities = values['authorities'] != null ? values['authorities'] : buildListOf(GrantedAuthority)

            new User(username: username, email: anyEmail(), firstName: anyLetterString(),
                     lastName: anyLetterString(), authorities: authorities)
        }

        registerBuilder(SimpleGrantedAuthority) {
            new SimpleGrantedAuthority(
                    anyFixedValue('ROLE_USER', 'ROLE_CLIENT', 'ROLE_ADMIN', 'ROLE_MASTER')
            )
        }


        registerBuilder(PersistableAuthorizationCode) { values ->
            new PersistableAuthorizationCode( anyLetterString(5, 10), build(OAuth2Authentication) )
        }


        registerBuilder(Client) { values ->
            def client = new Client(
                clientId: anyLetterString(5, 10),
                authorizedGrantTypes: anySet(enumValues(AuthorizationGrant), 1, 2),
                authorities: buildListOf(GrantedAuthority, 1, 3)
            )
            ObjectFeeder.populate(client)
            values.each { prop, value ->
                client[prop] = value
            }
            return client
        }

        registerBuilder(ClientDTO) { values ->
            def client = ObjectFeeder.populate(new ClientDTO()).with {
                authorities = buildListOf(GrantedAuthority, 0)*.authority
                return it
            }
            values.each { prop, value ->
                client[prop] = value
            }
            return client
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

        registerBuilder(Scope) {
            new Scope(anyLetterString(5, 10), anyLetterString(0, 10), anyBoolean())
        }

        //////// Superclasses ////////

        registerSuperclass OAuth2AccessToken, DefaultOAuth2AccessToken
        registerSuperclass OAuth2RefreshToken, DefaultOAuth2RefreshToken
        registerSuperclass ExpiringOAuth2RefreshToken, DefaultExpiringOAuth2RefreshToken
        registerSuperclass GrantedAuthority, SimpleGrantedAuthority
        registerSuperclass Authentication, UsernamePasswordAuthenticationToken
    }
}
