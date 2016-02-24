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
package cz.cvut.zuul.oaas.persistence

import cz.cvut.zuul.oaas.common.JSON
import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.persistence.support.JsonOAuth2AuthenticationConverter
import cz.cvut.zuul.oaas.persistence.support.OAuth2AuthenticationConverter
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication

import java.sql.Timestamp

import static cz.cvut.zuul.oaas.persistence.support.TimestampUtils.convertTimestamp

@Slf4j
@CompileStatic
@InheritConstructors
class JdbcAccessTokensRepo
        extends AbstractJdbcRepository<PersistableAccessToken, String> implements AccessTokensRepo {

    OAuth2AuthenticationConverter authenticationConverter = new JsonOAuth2AuthenticationConverter()

    final tableName = 'access_tokens'


    PersistableAccessToken findOneByAuthentication(OAuth2Authentication auth) {

        def authKey = PersistableAccessToken.extractAuthenticationKey(auth)

        def accessToken = queryOne """
            SELECT * FROM ${tableName}
            WHERE auth_key = ? AND expires_at > now() LIMIT 1
            """, authKey

        if (!accessToken) {
            log.debug 'Failed to find access token for authentication: [{}]', auth

        } else if (auth != accessToken.authentication) {
            log.debug 'Stored authentication details differs from given one, updating to keep the store consistent'
            delete accessToken  // XXX is this really needed?
            // Keep the store consistent (maybe the same user is represented by
            // this authentication but the details have changed).
            save new PersistableAccessToken(accessToken, auth)
        }
        accessToken
    }

    Collection<OAuth2AccessToken> findByClientId(String clientId) {
        findBy client_id: clientId
    }

    Collection<OAuth2AccessToken> findByClientIdAndUserName(String clientId, String userName) {
        findBy client_id: clientId, user_id: userName
    }

    void deleteByRefreshToken(OAuth2RefreshToken refreshToken) {
        deleteBy refresh_token: refreshToken.value
    }

    void deleteByClientId(String clientId) {
        deleteBy client_id: clientId
    }

    int deleteAllExpired() {
        jdbc.update "DELETE FROM ${tableName} WHERE expires_at < now()"
    }


    //////// ResultSet Mapping ////////

    PersistableAccessToken mapRow(Map row) {
        new PersistableAccessToken (
            value:                 row.id as String,
            expiration:            convertTimestamp(row.expires_at as Timestamp),
            tokenType:             row.token_type as String,
            refreshTokenValue:     row.refresh_token as String,
            scope:                 row.scopes as Set,
            additionalInformation: JSON.parse(row.extra_data as String),
            authentication:        authenticationConverter.deserialize(row.authentication as String)
        )
    }

    Map mapColumns(PersistableAccessToken obj) {
        [
            id:             obj.value,
            expires_at:     obj.expiration,
            token_type:     obj.tokenType,
            refresh_token:  obj.refreshTokenValue,
            scopes:         obj.scope.sort() as String[],
            extra_data:     JSON.serialize(obj.additionalInformation),
            client_id:      obj.authentication.OAuth2Request.clientId,
            user_id:        obj.authentication.userAuthentication?.name,
            auth_key:       obj.authenticationKey,
            authentication: authenticationConverter.serialize(obj.authentication)
        ]
    }
}
