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

import cz.cvut.zuul.oaas.models.PersistableAuthorizationCode
import cz.cvut.zuul.oaas.persistence.support.JsonOAuth2AuthenticationConverter
import cz.cvut.zuul.oaas.persistence.support.OAuth2AuthenticationConverter
import cz.cvut.zuul.oaas.repos.AuthorizationCodesRepo
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
@InheritConstructors
class JdbcAuthorizationCodesRepo
        extends AbstractJdbcRepository<PersistableAuthorizationCode, String> implements AuthorizationCodesRepo {

    OAuth2AuthenticationConverter authenticationConverter = new JsonOAuth2AuthenticationConverter()

    final tableName = 'authorization_codes'


    //////// ResultSet Mapping ////////

    PersistableAuthorizationCode mapRow(Map row) {
        new PersistableAuthorizationCode (
            row.id as String,
            authenticationConverter.deserialize(row.authentication as Serializable)
        )
    }

    Map mapColumns(PersistableAuthorizationCode obj) {
        [
            id:             obj.code,
            client_id:      obj.authentication.OAuth2Request.clientId,
            user_id:        obj.authentication.userAuthentication?.name,
            authentication: authenticationConverter.serialize(obj.authentication)
        ]
    }
}
