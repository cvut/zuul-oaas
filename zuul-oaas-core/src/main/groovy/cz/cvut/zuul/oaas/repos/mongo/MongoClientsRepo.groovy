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

import com.mongodb.WriteResult
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.repos.ClientsRepo
import groovy.transform.InheritConstructors
import org.springframework.dao.EmptyResultDataAccessException

import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query
import static org.springframework.data.mongodb.core.query.Update.update

@InheritConstructors
class MongoClientsRepo extends AbstractMongoRepository<Client, String> implements ClientsRepo {


    void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException {
        WriteResult result = mongo.updateFirst(
                query(where("_id").is(clientId)),
                update("secret", secret),
                entityClass)

        if (result.n == 0) {
            throw new EmptyResultDataAccessException("No such client with clientId = ${clientId}", 1)
        }
    }
}
