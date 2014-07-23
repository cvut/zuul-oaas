/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.it

import cz.cvut.zuul.oaas.Application
import cz.cvut.zuul.oaas.it.config.TestMongoPersistenceConfig
import cz.cvut.zuul.oaas.it.support.Fixtures
import cz.cvut.zuul.oaas.it.support.MyResponseEntity
import cz.cvut.zuul.oaas.it.support.RestTemplateDSL
import groovy.util.logging.Slf4j
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Shared
import spock.lang.Specification

import javax.annotation.PostConstruct
import javax.inject.Inject

import static cz.cvut.zuul.oaas.it.support.TestUtils.isUUID
import static cz.cvut.zuul.oaas.it.support.TestUtils.parseCookie

@Slf4j
@WebAppConfiguration
// pick up randomly selected port
@IntegrationTest('server.port=0')
@ContextConfiguration(classes=[Application, TestMongoPersistenceConfig], loader=SpringApplicationContextLoader)
abstract class AbstractHttpIntegrationTest extends Specification {

    @Delegate(includes=['GET', 'POST'])
    private RestTemplateDSL httpClient

    @Shared String serverUri

    @Inject Environment env

    @Inject MongoTemplate mongoTemplate

    @Inject TokenStore tokenStore

    protected MyResponseEntity r

    static {
        // Override active profile for tests.
        // @ActiveProfiles nor @IntegrationTest('spring.profiles.active=test') didn't work here.
        System.setProperty('spring.profiles.active', 'test')
    }

    @Inject
    private setupHttpClient(EmbeddedWebApplicationContext server) {
        serverUri = 'http://localhost:' + server.embeddedServletContainer.port
        httpClient = new RestTemplateDSL(defaultBaseUri: serverUri, defaultRequestOpts: defaultRequestOpts)
    }

    @PostConstruct prepareDatabase() {
        log.info 'Dropping database'
        dropDatabase()

        log.info 'Loading seed data'
        loadSeed()
    }


    // to be overridden in subclasses
    def getDefaultRequestOpts() { [:] }

    def <T> T $(String propertyKey, Class<T> type = String) {
        env.getProperty(propertyKey, type)
    }

    def loadSeed() {
        mongoTemplate.save(Fixtures.allGrantsClient())
    }

    def dropCollection(Class entityClass) {
        mongoTemplate.dropCollection(entityClass)
    }

    def dropDatabase() {
        mongoTemplate.db.dropDatabase()
    }

    def loginUserAndGetCookie() {
       def response =
            POST '/login.do',
            ContentType: 'application/x-www-form-urlencoded',
            Accept: '*/*',
            body: [j_username: 'tomy', j_password: 'best']

        parseCookie(response.headers)
    }


    void assertAccessToken(String accessToken) {
        assert isUUID(accessToken)
        assert tokenStore.readAccessToken(accessToken)
    }

    void assertRefreshToken(String refreshToken) {
        assert isUUID(refreshToken)
        assert tokenStore.readRefreshToken(refreshToken)
    }
}
