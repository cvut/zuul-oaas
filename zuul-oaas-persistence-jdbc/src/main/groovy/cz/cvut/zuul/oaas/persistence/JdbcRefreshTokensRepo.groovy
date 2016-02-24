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

import cz.cvut.zuul.oaas.models.PersistableRefreshToken
import cz.cvut.zuul.oaas.persistence.support.JsonOAuth2AuthenticationConverter
import cz.cvut.zuul.oaas.persistence.support.OAuth2AuthenticationConverter
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

import java.sql.Timestamp

import static cz.cvut.zuul.oaas.persistence.support.TimestampUtils.convertTimestamp

@CompileStatic
@InheritConstructors
class JdbcRefreshTokensRepo
        extends AbstractJdbcRepository<PersistableRefreshToken, String> implements RefreshTokensRepo {

    OAuth2AuthenticationConverter authenticationConverter = new JsonOAuth2AuthenticationConverter()

    final tableName = 'refresh_tokens'


    void deleteByClientId(String clientId) {
        deleteBy client_id: clientId
    }

    int deleteAllExpired() {
        jdbc.update "DELETE FROM ${tableName} WHERE expires_at < now()"
    }


    //////// ResultSet Mapping ////////

    PersistableRefreshToken mapRow(Map row) {
        new PersistableRefreshToken (
            row.id as String,
            convertTimestamp(row.expires_at as Timestamp),
            authenticationConverter.deserialize(row.authentication as Serializable)
        )
    }

    Map mapColumns(PersistableRefreshToken obj) {
        [
            id:             obj.value,
            expires_at:     obj.expiration,
            client_id:      obj.authentication.OAuth2Request?.clientId,
            user_id:        obj.authentication.userAuthentication?.name,
            authentication: authenticationConverter.serialize(obj.authentication)
        ]
    }
}
