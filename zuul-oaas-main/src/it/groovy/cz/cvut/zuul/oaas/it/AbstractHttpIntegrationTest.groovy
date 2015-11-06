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
import cz.cvut.zuul.oaas.it.config.EmbeddedJettyConfig
import cz.cvut.zuul.oaas.it.config.TestMongoPersistenceConfig
import cz.cvut.zuul.oaas.it.support.Fixtures
import cz.cvut.zuul.oaas.it.support.HttpResponseAssertDSL
import cz.cvut.zuul.oaas.it.support.MyResponseEntity
import cz.cvut.zuul.oaas.it.support.RestTemplateDSL
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
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

import static cz.cvut.zuul.oaas.it.support.TestUtils.isUUID
import static cz.cvut.zuul.oaas.it.support.TestUtils.parseCookie

@Slf4j
@WebAppConfiguration
// pick up randomly selected port
@IntegrationTest('server.port=0')
@ContextConfiguration(
    loader = SpringApplicationContextLoader,
    classes = [Application, EmbeddedJettyConfig, TestMongoPersistenceConfig])
abstract class AbstractHttpIntegrationTest extends Specification {

    @Shared
    private boolean initialized

    @Shared String serverUri

    @Autowired Environment env

    @Autowired MongoTemplate mongoTemplate

    @Autowired TokenStore tokenStore

    private RestTemplateDSL httpClient

    protected MyResponseEntity response


    static {
        // Override active profile for tests.
        // @ActiveProfiles nor @IntegrationTest('spring.profiles.active=test') didn't work here.
        System.setProperty('spring.profiles.active', 'test')
    }

    @Autowired
    private setupHttpClient(EmbeddedWebApplicationContext server) {
        serverUri = 'http://localhost:' + server.embeddedServletContainer.port
        httpClient = new RestTemplateDSL(defaultBaseUri: serverUri, defaultRequestOpts: defaultRequestOpts)
    }

    def setup() {
        // workaround; in setupSpec phase beans are not injected yet
        if (!initialized) {
            prepareSpec()
            initialized = true
        }
    }

    def prepareSpec() {
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

    MyResponseEntity send(Map<String, ?> kwargs) {
        response = httpClient.perform(kwargs)
    }

    void check(Map<String, ?> kwargs, status=-1) {
        if (status != -1) {
            kwargs['status'] = status
        }
        new HttpResponseAssertDSL().expect(kwargs, response)
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
       def resp = send(
           POST: '/login.do',
           ContentType: 'application/x-www-form-urlencoded',
           Accept: '*/*',
           body: [j_username: 'zuul', j_password: 'zuul']
       )

        parseCookie(resp.headers.getFirst('Set-Cookie'))
    }


    def isValidAccessToken(String accessToken) {
        isUUID(accessToken) && tokenStore.readAccessToken(accessToken)
    }

    def isValidRefreshToken(String refreshToken) {
        isUUID(refreshToken) && tokenStore.readRefreshToken(refreshToken)
    }
}
