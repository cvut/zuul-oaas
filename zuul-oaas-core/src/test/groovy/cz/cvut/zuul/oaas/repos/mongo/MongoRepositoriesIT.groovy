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
package cz.cvut.zuul.oaas.repos.mongo

import cz.cvut.zuul.oaas.config.TestMongoPersistenceConfig
import cz.cvut.zuul.oaas.repos.AccessTokensRepoIT
import cz.cvut.zuul.oaas.repos.ApprovalsRepoIT
import cz.cvut.zuul.oaas.repos.AuthorizationCodesRepoIT
import cz.cvut.zuul.oaas.repos.ClientsRepoIT
import cz.cvut.zuul.oaas.repos.RefreshTokensRepoIT
import cz.cvut.zuul.oaas.repos.ResourcesRepoIT
import groovy.transform.AnnotationCollector
import org.junit.After
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ContextConfiguration


@MongoTestContext
class MongoAccessTokensRepoIT extends AccessTokensRepoIT implements MongoTestCleanup {}

@MongoTestContext
class MongoApprovalsRepoIT extends ApprovalsRepoIT implements MongoTestCleanup {}

@MongoTestContext
class MongoAuthorizationCodesRepoIT extends AuthorizationCodesRepoIT implements MongoTestCleanup {}

@MongoTestContext
class MongoClientsRepoIT extends ClientsRepoIT implements MongoTestCleanup {}

@MongoTestContext
class MongoRefreshTokensRepoIT extends RefreshTokensRepoIT implements MongoTestCleanup {}

@MongoTestContext
class MongoResourcesRepoIT extends ResourcesRepoIT implements MongoTestCleanup {}


trait MongoTestCleanup {

    @Autowired
    private MongoTemplate mongoTemplate

    @After cleanup() {
        mongoTemplate.collectionNames
            .findAll { !it.startsWith('system.') }
            .each(mongoTemplate.&dropCollection)
    }
}

@AnnotationCollector
@ContextConfiguration(classes = TestMongoPersistenceConfig)
@interface MongoTestContext {}
