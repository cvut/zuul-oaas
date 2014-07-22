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

import cz.cvut.zuul.oaas.it.support.Fixtures
import cz.cvut.zuul.oaas.models.PersistableAccessToken

import static cz.cvut.zuul.oaas.it.support.TestUtils.base64
import static java.lang.Math.abs

class ClientCredentialsGrantIT extends AbstractHttpIntegrationTest {

    def client = Fixtures.allGrantsClient()
    def credentials = client.clientId + ':' + client.clientSecret


    def 'request access token with grant client_credentials'() {
        setup:
            dropCollection PersistableAccessToken
        and:
            def expectedExpires = $('oaas.access_token.validity') as int
            assert expectedExpires > 0

        when: 'send request to the token endpoint'
            r = POST '/oauth/token',
                ContentType: 'application/x-www-form-urlencoded',
                Authorization: "Basic ${base64(credentials)}",
                body: [grant_type: 'client_credentials', scope: client.scope[0]]

        then: 'should get success response with JSON'
            r.status == 200
            r.headers['Content-Type'][0].contains 'application/json'
            r.body.json['token_type'] == 'bearer'

        and: 'access token is valid and exists'
            assertAccessToken r.body.json['access_token'] as String

        and: 'scope contains only the requested scope'
            r.body.json['scope'] == client.scope[0]

        and: 'expires_in is cca the same as configured validity duration'
            def actualExpires = r.body.json['expires_in'] as int
            abs( actualExpires - expectedExpires ) < 5
    }
}
