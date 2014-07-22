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
import cz.cvut.zuul.oaas.it.support.MyResponseEntity
import spock.lang.Unroll

import static cz.cvut.zuul.oaas.it.support.TestUtils.base64

class TokenEndpointIT extends AbstractHttpIntegrationTest {

    def client = Fixtures.allGrantsClient()

    def defaultRequestOpts = [
            ContentType: 'application/x-www-form-urlencoded',
            body: [grant_type: 'client_credentials']
    ]

    MyResponseEntity r


    def 'request token without authorization'() {
        when:
            r = POST '/oauth/token'
        then:
            r.status == 401
    }

    def 'request token with invalid credentials'() {

        when: 'using Authorization header'
            r = POST '/oauth/token',
                Authorization: "Basic ${base64('test-client:invalid')}"
        then:
            r.status in [401, 403]

        when: 'using form parameters'
            r = POST '/oauth/token',
                body: [grant_type: 'client_credentials', client_id: client.clientId, client_secret: 'invalid']
        then:
            r.status in [401, 403]
    }

    @Unroll
    def 'request token with #desc'() {
        given:
            def credentials = "${client.clientId}:${client.clientSecret}"
        when:
            r = POST '/oauth/token',
                Authorization: "Basic ${base64(credentials)}",
                body: body
        then:
            r.status == 400
        where:
            body                                                | desc
            [grant_type: 'implicit', scope: 'urn:zuul:invalid'] | 'unregistered scope'
            [grant_type: 'invalid']                             | 'invalid grant type'
    }
}
