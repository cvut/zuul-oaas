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

import cz.cvut.zuul.oaas.models.PersistableApproval
import cz.cvut.zuul.oaas.repos.ApprovalsRepo
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

import java.sql.Timestamp

import static cz.cvut.zuul.oaas.persistence.support.TimestampUtils.convertTimestamp

@CompileStatic
@InheritConstructors
class JdbcApprovalsRepo extends AbstractJdbcRepository<PersistableApproval, Serializable> implements ApprovalsRepo {

    final tableName = 'approvals'


    PersistableApproval findOne(String userId, String clientId, String scope) {
        def results = findBy user_id: userId, client_id: clientId, scope: scope

        results.empty ? null : results.first()
    }

    Collection<PersistableApproval> findByUserIdAndClientId(String userId, String clientId) {
        findBy user_id: userId, client_id: clientId
    }

    boolean exists(String userId, String clientId, String scope) {
        jdbc.queryForObject """
            SELECT EXISTS (
                SELECT 1 FROM ${tableName}
                WHERE user_id = ? AND client_id = ? AND scope = ?
            )
            """, Boolean, userId, clientId, scope
    }

    void deleteById(String userId, String clientId, String scope) {
        jdbc.update """
            DELETE FROM ${tableName}
            WHERE user_id = ? AND client_id = ? AND scope = ?
            """, userId, clientId, scope
    }


    //////// ResultSet Mapping ////////

    PersistableApproval mapRow(Map row) {
        new PersistableApproval (
            row.user_id as String,
            row.client_id as String,
            row.scope as String,
            row.approved as boolean,
            convertTimestamp(row.expires_at as Timestamp)
        )
    }

    Map mapColumns(PersistableApproval obj) {
        [
            id:         obj.id,
            user_id:    obj.userId,
            client_id:  obj.clientId,
            scope:      obj.scope,
            approved:   obj.approved,
            expires_at: obj.expiresAt
        ]
    }
}
