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
package cz.cvut.zuul.oaas.endpoints

import cz.cvut.zuul.oaas.common.JSON
import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.cache2k.CacheBuilder
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON

class CachingCheckTokenEndpointTest extends Specification {

    @Delegate
    CoreObjectFactory factory = new CoreObjectFactory()

    def accessTokensRepo = Mock(AccessTokensRepo)

    def endpoint = new CachingCheckTokenEndpoint(accessTokensRepo, CacheBuilder.newCache(String, Map))

    def token = build(PersistableAccessToken)

    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(endpoint)
        .defaultRequest(MockMvcRequestBuilders.get('/')
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON))
        .build()


    def 'GET /oauth/check_token: returns success response when given valid token'() {
        setup:
            1 * accessTokensRepo.findOne('abc') >> token
        when:
            def response = GET '/oauth/check_token?token=abc'
        then:
            assertResponse response, token
    }

    def 'GET /oauth/check_token: returns response from cache when called for 2nd time'() {
        when:
            def resp1 = GET '/oauth/check_token?token=abc'
            def resp2 = GET '/oauth/check_token?token=abc'
        then:
            1 * accessTokensRepo.findOne('abc') >> token
            assertResponse resp1, token
            assertResponse resp2, token
    }

    def 'GET /oauth/check_token: returns 400 when token is not found'() {
        setup:
            accessTokensRepo.findOne('xxx') >> null
        when:
            def response = GET '/oauth/check_token?token=xxx'
        then:
            response.status == 400
    }


    private void assertResponse(MockHttpServletResponse response, PersistableAccessToken token) {

        assert response.status == 200
        assert response.contentType == 'application/json;charset=UTF-8'

        def json = JSON.parse(response.contentAsString)
        assert json.client_id          == token.authenticatedClientId
        assert json.exp                == token.expiration.time / 1000 as int
        assert json.scope as Set       == token.scope
        assert json.user_name          == token.authenticatedUsername
        assert json.aud as Set         == token.authentication.OAuth2Request.resourceIds
        assert json.authorities as Set == token.authentication.authorities*.toString() as Set
    }

    private GET(String url) {
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andReturn().response
    }
}
