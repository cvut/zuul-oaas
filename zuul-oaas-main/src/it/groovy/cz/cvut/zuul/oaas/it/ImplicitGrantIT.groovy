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
import spock.lang.Stepwise

import static org.springframework.http.HttpStatus.FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.TEXT_HTML
import static org.springframework.security.oauth2.provider.AuthorizationRequest.USER_OAUTH_APPROVAL

@Stepwise
class ImplicitGrantIT extends AbstractHttpIntegrationTest {

    static final ASSERT_CONFIRMATION_PAGE = {
        assert it.str.contains(USER_OAUTH_APPROVAL)
    }

    final client = Fixtures.allGrantsClient()

    final defaultRequestOpts = [
            Accept: 'text/html, application/xhtml+xml'
    ]

    def authzParams = [
            response_type: 'token',
            client_id: client.clientId,
            redirect_uri: client.registeredRedirectUri[0],
            scope: client.scope[0]
    ]


    def 'request user authorization and approve access'() {
        setup:
            dropCollection PersistableAccessToken
        and:
            def cookie = loginUserAndGetCookie()

        when: 'request authorization with response_type token'
        send  GET: '/oauth/authorize', query: authzParams,
              Cookie: cookie

        then:
        check status: OK,
              ContentType: TEXT_HTML,
              body: ASSERT_CONFIRMATION_PAGE

        when: 'approve client authorization'
        send  POST: '/oauth/authorize',
              ContentType: 'application/x-www-form-urlencoded',
              Cookie: cookie,
              body: [(USER_OAUTH_APPROVAL): 'true']

        then: 'should be redirected to the redirect_uri with access token in fragment'
        check status: FOUND,
              Location: {
                  assert it.startsWith(authzParams['redirect_uri'] + '#') &
                         it.contains('token_type=bearer') &
                         ! it.contains('refresh_token=')
                  def token = (it =~ 'access_token=([^&]+)')[0][1]
                  assert isValidAccessToken(token)
              }
    }


    def 'request user authorization when client is already authorized'() {
        setup:
            def cookie = loginUserAndGetCookie()

        when: 'request authorization with response_type token'
        send  GET: '/oauth/authorize', query: authzParams,
              Cookie: cookie

        then: 'should get authorization code without asking for confirmation'
        check status: FOUND,
              Location: {
                  assert it.startsWith(authzParams['redirect_uri'] + '#') &&
                         it.contains('token_type=bearer')
                  def token = (it =~ 'access_token=([^&]+)')[0][1]
                  assert isValidAccessToken(token)
              }
    }


    def 'request user authorization and reject access'() {
        setup:
            dropCollection PersistableAccessToken
        and:
            authzParams += [redirect_uri: client.registeredRedirectUri[1]]
            def cookie = loginUserAndGetCookie()

        when: 'request authorization with response_type token'
        send  GET: '/oauth/authorize', query: authzParams,
              Cookie: cookie

        then:
        check status: OK,
              ContentType: TEXT_HTML,
              body: ASSERT_CONFIRMATION_PAGE

        when: 'reject client authorization'
        send  POST: '/oauth/authorize',
              ContentType: 'application/x-www-form-urlencoded',
              Cookie: cookie,
              body: [(USER_OAUTH_APPROVAL): 'false']

        then: 'should be redirected to the redirect_uri with error access_denied'
        check status: FOUND,
              Location: {
                  assert it.startsWith(authzParams['redirect_uri'] + '#') &
                         it.contains('error=access_denied')
                  assert ! it.contains('access_token=')
              }
    }
}
