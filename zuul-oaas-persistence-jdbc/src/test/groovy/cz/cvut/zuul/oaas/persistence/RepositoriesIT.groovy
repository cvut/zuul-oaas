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

import cz.cvut.zuul.oaas.persistence.config.JdbcPersistenceConfig
import cz.cvut.zuul.oaas.repos.AccessTokensRepoIT
import cz.cvut.zuul.oaas.repos.ApprovalsRepoIT
import cz.cvut.zuul.oaas.repos.AuthorizationCodesRepoIT
import cz.cvut.zuul.oaas.repos.ClientsRepoIT
import cz.cvut.zuul.oaas.repos.RefreshTokensRepoIT
import cz.cvut.zuul.oaas.repos.RepositoriesCleaner
import cz.cvut.zuul.oaas.repos.ResourcesRepoIT
import groovy.transform.AnnotationCollector
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.IgnoreIf

import static TestUtils.isPortInUse


@JdbcTestContext
class JdbcAccessTokensRepoIT extends AccessTokensRepoIT implements JdbcTestCleanup {}

@JdbcTestContext
class JdbcApprovalsRepoIT extends ApprovalsRepoIT implements JdbcTestCleanup {}

@JdbcTestContext
class JdbcAuthorizationCodesRepoIT extends AuthorizationCodesRepoIT implements JdbcTestCleanup {}

@JdbcTestContext
class JdbcClientsRepoIT extends ClientsRepoIT implements JdbcTestCleanup {}

@JdbcTestContext
class JdbcRefreshTokensRepoIT extends RefreshTokensRepoIT implements JdbcTestCleanup {}

@JdbcTestContext
class JdbcResourcesRepoIT extends ResourcesRepoIT implements JdbcTestCleanup {}


trait JdbcTestCleanup {

    @Autowired
    private RepositoriesCleaner cleaner

    @Before cleanup() {
        cleaner.cleanAllRepositories()
    }
}

@AnnotationCollector
@IgnoreIf({ !isPortInUse('localhost', 5432) })
@ActiveProfiles('test')
@TestPropertySource(properties = [
    'persistence.jdbc.host=localhost',
    'persistence.jdbc.port=5432',
    'persistence.jdbc.dbname=zuul_oaas_test',
    'persistence.jdbc.username=postgres',
    'persistence.jdbc.password=',
    'persistence.jdbc.data_source_class=com.impossibl.postgres.jdbc.PGDataSource',
    'persistence.jdbc.pool.max_size=3',
    'persistence.jdbc.pool.min_idle=3',
    'persistence.jdbc.pool.max_lifetime=60',
    'persistence.jdbc.pool.idle_timeout=60'
])
@ContextConfiguration(classes = JdbcPersistenceConfig)
@interface JdbcTestContext {}
