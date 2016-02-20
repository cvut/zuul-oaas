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
package cz.cvut.zuul.oaas.it

import cz.cvut.zuul.oaas.it.support.Fixtures
import cz.cvut.zuul.oaas.repos.AccessTokensRepo

import static cz.cvut.zuul.oaas.it.support.TestUtils.base64
import static java.lang.Math.abs
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON

class ClientCredentialsGrantIT extends AbstractHttpIntegrationTest {

    def client = Fixtures.allGrantsClient()
    def credentials = client.clientId + ':' + client.clientSecret


    def 'request access token with grant client_credentials'() {
        setup:
            cleanRepositories AccessTokensRepo
        and:
            def expectedExpires = p('oaas.access_token.validity') as int
            assert expectedExpires > 0

        when: 'send request to the token endpoint'
        send  POST: '/oauth/token',
              ContentType: 'application/x-www-form-urlencoded',
              Authorization: "Basic ${base64(credentials)}",
              body: [grant_type: 'client_credentials', scope: client.scope[0]]

        then: 'should get valid access token for the requested scope (only)'
        check status: OK,
              ContentType: APPLICATION_JSON,
              body: { body ->
                  assert body.json['token_type'] == 'bearer'
                  assert body.json['scope'] == client.scope[0]
                  assert isValidAccessToken(body.json['access_token'])

                  def actualExpires = body.json['expires_in'] as int
                  assert abs( actualExpires - expectedExpires ) < 5
              }
    }
}
