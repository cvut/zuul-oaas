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
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON

class CheckTokenEndpointIT extends AbstractHttpIntegrationTest {

    def defaultRequestOpts = [
        ContentType: 'application/x-www-form-urlencoded'
    ]


    def 'check valid access token'() {
        given:
            def token = Fixtures.validAccessToken()
        when:
        send POST: '/oauth/check_token',
             body: [token: token.value]
        then:
        check status: OK,
              ContentType: APPLICATION_JSON,
              body: { body ->
                  assert body.json.client_id    == token.authenticatedClientId
                  assert body.json.exp          == token.expiration.time / 1000 as int
                  assert body.json.scope as Set == token.scope
                  assert body.json.aud as Set   == token.authentication.OAuth2Request.resourceIds
              }
    }

    @Unroll
    def 'check #desc access token'() {
        when:
        send POST: '/oauth/check_token',
             body: [token: tokenValue]
        then:
            response.status == 400
        where:
            tokenValue                             || desc
            'f463cf73-a33f-4dd0-b92a-dce8e11bcc8c' || 'non-existing'
            '603f3b3d-d918-4226-aadd-ae08563bd9c1' || 'expired'
    }
}
