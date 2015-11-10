/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
import cz.cvut.zuul.oaas.models.PersistableApproval
import spock.lang.Stepwise

import static cz.cvut.zuul.oaas.it.support.RestTemplateDSL.formatQuery
import static cz.cvut.zuul.oaas.it.support.TestUtils.base64
import static cz.cvut.zuul.oaas.it.support.TestUtils.parseCookie
import static org.springframework.http.HttpStatus.FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.TEXT_HTML
import static org.springframework.security.oauth2.common.util.OAuth2Utils.USER_OAUTH_APPROVAL

@Stepwise
class AuthorizationCodeGrantIT extends AbstractHttpIntegrationTest {

    static final ASSERT_LOGIN_PAGE = {
        assert it.str =~ /<input[^>]*name="j_username"/
        assert it.str =~ /<input[^>]*name="j_password"/
    }

    static final ASSERT_CONFIRMATION_PAGE = {
        assert it.str.contains(USER_OAUTH_APPROVAL)
    }

    final client = Fixtures.allGrantsClient()
    final credentials = "${client.clientId}:${client.clientSecret}"

    final defaultRequestOpts = [
            Accept: 'text/html, application/xhtml+xml'
    ]

    def authzParams = [
            response_type: 'code',
            state: 'xyz',
            client_id: client.clientId,
            redirect_uri: client.registeredRedirectUri[0],
            scope: client.scope[0]
    ]


    def 'request user authorization, log-in user, approve access and obtain access token'() {
        setup:
            def cookie = null
            def code = null
        and:
            dropCollection PersistableAccessToken
            dropCollection PersistableApproval

        when: 'request authorization with response_type code'
        send  GET: '/oauth/authorize', query: authzParams

        then:
        check status: FOUND,
              Location: "$serverUri/login.html",
              SetCookie: { cookie = parseCookie(it) }

        when: 'follow redirect to the login page'
        send  GET: '/login.html', Cookie: cookie

        then:
        check status: OK,
              ContentType: TEXT_HTML,
              body: ASSERT_LOGIN_PAGE

        when: 'log-in user with valid credentials'
        send  POST: '/login.do',
              ContentType: 'application/x-www-form-urlencoded',
              Cookie: cookie,
              body: [j_username: 'zuul', j_password: 'zuul']

        then: 'should be redirected back to the authorization endpoint with the same query as in the first step'
        check status: FOUND,
              Location: "${serverUri}/oauth/authorize?${formatQuery(authzParams)}",
              SetCookie: { cookie = it ? parseCookie(it) : cookie }

        when: 'follow redirection back to the authorization endpoint'
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

        then: 'should be redirected to the specified redirect_uri with authorization code'
        check status: FOUND,
              Location: {
                  assert it.startsWith(authzParams['redirect_uri']) &&
                         it.contains('code=') &&
                         it.contains("state=${authzParams['state']}")
                  code = (it =~ /code=(.*)&/)[0][1]
              }

        when: 'send authorized request to the token endpoint'
        send  POST: '/oauth/token',
              ContentType: 'application/x-www-form-urlencoded',
              Accept: 'application/json',
              Authorization: "Basic ${base64(credentials)}",
              body: [code: code, grant_type: 'authorization_code', redirect_uri: authzParams['redirect_uri']]

        then:
        check status: OK,
              ContentType: APPLICATION_JSON,
              body: { body ->
                  assert body.json['token_type'] == 'bearer'
                  assert body.json['expires_in'] > 0
                  assert body.json['scope'] == client.scope[0]
                  assert isValidAccessToken( body.json['access_token'] )
                  assert isValidRefreshToken( body.json['refresh_token'] )
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
                  assert it.startsWith(authzParams['redirect_uri']) && it.contains('code=')
            }
    }


    def 'request user authorization when client is already authorized but with different scopes'() {
        setup:
            authzParams += [scope: client.scope.join(' ')]
            def cookie = loginUserAndGetCookie()

        when: 'request authorization with response_type token'
        send  GET: '/oauth/authorize', query: authzParams,
              Cookie: cookie

        then: 'should be asked to confirm access'
        check status: OK,
              ContentType: TEXT_HTML,
              body: ASSERT_CONFIRMATION_PAGE
    }


    def 'request user authorization and reject access'() {
        setup:
            dropCollection PersistableAccessToken
            dropCollection PersistableApproval
        and:
            authzParams += [state: 'zyx', redirect_uri: client.registeredRedirectUri[1]]
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

        then: 'should be redirected to the specified redirect_uri with error access_denied'
        check status: FOUND,
              Location: {
                  assert it.startsWith(authzParams['redirect_uri']) &
                         it.contains('error=access_denied') &
                         it.contains("state=${authzParams['state']}")
                  assert ! it.contains('code=')
              }
    }
}
