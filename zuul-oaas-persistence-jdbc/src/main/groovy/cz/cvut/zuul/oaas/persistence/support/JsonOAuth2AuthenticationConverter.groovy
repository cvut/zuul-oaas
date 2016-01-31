/*
 * The MIT License
 *
 * Copyright 2013-2016 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.persistence.support

import cz.cvut.zuul.oaas.common.JSON
import cz.cvut.zuul.oaas.models.User
import groovy.transform.CompileStatic
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request

import static org.springframework.security.core.authority.AuthorityUtils.authorityListToSet
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList

@CompileStatic
class JsonOAuth2AuthenticationConverter implements OAuth2AuthenticationConverter {


    String serialize(OAuth2Authentication obj) {
        if (!obj) return null

        JSON.serialize([
            oauth2Request: obj.OAuth2Request?.with {[
                clientId:          it.clientId,
                scope:             it.scope,
                requestParameters: it.requestParameters,
                resourceIds:       it.resourceIds,
                authorities:       authorityListToSet(it.authorities),
                approved:          it.approved,
                redirectUri:       it.redirectUri,
                responseTypes:     it.responseTypes,
                extensions:        it.extensions
            ]},
            userAuthentication: obj.userAuthentication?.with {[
                username:    it.name,
                email:       principalEmail(it.principal),
                authorities: authorityListToSet(it.authorities)
            ]}
        ])
    }

    OAuth2Authentication deserialize(Serializable input) {
        if (!input) return null

        def map = JSON.parse(input.toString())

        new OAuth2Authentication (
            createOAuthRequest(map.oauth2Request as Map),
            createAuthentication(map.userAuthentication as Map)
        )
    }


    private OAuth2Request createOAuthRequest(Map map) {
        if (!map) return null

        new OAuth2Request (
            map.requestParameters as Map,
            map.clientId as String,
            createAuthorityList(map.authorities as String[]),
            map.approved as boolean,
            map.scope as Set,
            map.resourceIds as Set,
            map.redirectUri as String,
            map.responseTypes as Set,
            map.extensions as Map
        )
    }

    private Authentication createAuthentication(Map map) {
        if (!map) return null

        def authorities = createAuthorityList(map.authorities as String[])
        def principal = new User (
            username: map.username as String,
            email: map.email as String,
            authorities: (authorities ?: []) as Set
        )
        new UsernamePasswordAuthenticationToken(principal, null, authorities)
    }

    private static principalEmail(principal) {
        if (principal instanceof User) {
            principal.email
        }
    }
}
