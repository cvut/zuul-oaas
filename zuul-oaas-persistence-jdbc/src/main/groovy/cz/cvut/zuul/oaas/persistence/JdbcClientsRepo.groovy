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

import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.repos.ClientsRepo
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.dao.EmptyResultDataAccessException

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList

@CompileStatic
@InheritConstructors
class JdbcClientsRepo extends AbstractJdbcRepository<Client, String> implements ClientsRepo {

    final tableName = 'clients'


    void updateClientSecret(String clientId, String secret) {

        def changedRows = jdbc.update("UPDATE ${tableName} SET secret = ? WHERE id = ?",
                                      secret, clientId)
        if (changedRows == 0) {
            throw new EmptyResultDataAccessException('No row changed', 1)
        }
    }


    //////// ResultSet Mapping ////////

    Client mapRow(Map row) {
        new Client (
            clientId:                    row.id as String,
            clientSecret:                row.secret as String,
            scope:                       row.scopes as Set,
            resourceIds:                 row.resource_ids as Set,
            authorizedGrantTypes:        row.grant_types as Set,
            registeredRedirectUri:       row.redirect_uris as Set,
            authorities:                 createAuthorityList(row.authorities as String[]),
            accessTokenValiditySeconds:  row.access_token_validity as Integer,
            refreshTokenValiditySeconds: row.refresh_token_validity as Integer,
            displayName:                 row.display_name as String,
            locked:                      row.locked as boolean,
            userApprovalRequired:        row.user_approval_required as boolean
        )
    }

    Map mapColumns(Client obj) {
        [
            id:                     obj.clientId,
            secret:                 obj.clientSecret,
            scopes:                 obj.scope.sort() as String[],
            resource_ids:           obj.resourceIds.sort() as String[],
            grant_types:            obj.authorizedGrantTypes.sort() as String[],
            redirect_uris:          obj.registeredRedirectUri.sort() as String[],
            authorities:            obj.authorities*.toString().sort() as String[],
            access_token_validity:  obj.accessTokenValiditySeconds,
            refresh_token_validity: obj.refreshTokenValiditySeconds,
            display_name:           obj.displayName,
            locked:                 obj.locked,
            user_approval_required: obj.userApprovalRequired
        ]
    }
}
