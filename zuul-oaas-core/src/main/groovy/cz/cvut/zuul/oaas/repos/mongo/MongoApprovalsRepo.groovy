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
package cz.cvut.zuul.oaas.repos.mongo

import cz.cvut.zuul.oaas.models.PersistableApproval
import cz.cvut.zuul.oaas.repos.ApprovalsRepo
import groovy.transform.InheritConstructors

import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query

@InheritConstructors
class MongoApprovalsRepo extends AbstractMongoRepository<PersistableApproval, Serializable> implements ApprovalsRepo {

    PersistableApproval findOne(String userId, String clientId, String scope) {
        mongo.findOne( queryById(userId, clientId, scope), entityClass )
    }

    Collection<PersistableApproval> findByUserIdAndClientId(String userId, String clientId) {
        mongo.find(
            query( where('userId').is(userId).and('clientId').is(clientId) ),
            entityClass)
    }

    Collection<String> findValidApprovedScopes(String userId, String clientId) {
        def query = query (
            where('userId').is(userId)
                .and('clientId').is(clientId)
                .and('approved').is(true)
                .and('expiresAt').gt(new Date())
        )
        query.fields().include('scope')

        mongo.find(query, entityClass)*.scope
    }

    boolean exists(String userId, String clientId, String scope) {
        mongo.exists( queryById(userId, clientId, scope), entityClass )
    }

    void deleteById(String userId, String clientId, String scope) {
        mongo.remove( queryById(userId, clientId, scope), entityClass )
    }


    private queryById(userId, clientId, scope) {
        query( where('userId').is(userId).and('clientId').is(clientId).and('scope').is(scope) )
    }
}
