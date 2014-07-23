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

import static cz.cvut.zuul.oaas.it.support.RestTemplateDSL.formatQuery
import static cz.cvut.zuul.oaas.it.support.TestUtils.base64
import static cz.cvut.zuul.oaas.it.support.TestUtils.parseCookie
import static org.springframework.security.oauth2.provider.AuthorizationRequest.USER_OAUTH_APPROVAL

class AuthorizationCodeGrantIT extends AbstractHttpIntegrationTest {

    def client = Fixtures.allGrantsClient()
    def credentials = "${client.clientId}:${client.clientSecret}"

    def defaultRequestOpts = [
            Accept: 'text/html, application/xhtml+xml'
    ]


    def 'request user authorization, approve access and obtain access token'() {
        setup:
            dropCollection PersistableAccessToken
        and:
            def redirectUri = client.registeredRedirectUri[0]
            def authzParams = [response_type: 'code', state: 'xyz',
                               client_id: client.clientId, redirect_uri: redirectUri, scope: client.scope[0]]

        when: 'send request to the authorization endpoint'
            r = GET '/oauth/authorize', query: authzParams

        then: 'should get a Cookie and be redirected to the login page'
            def location1 = r.headers['Location'][0]
            def cookie1 = parseCookie(r.headers)
        and:
            r.status == 302
            cookie1 != null
            location1 == "$serverUri/login.html"

        when: 'follow redirect to the login page'
            r = GET location1,
                Cookie: cookie1

        then: 'should get HTML page that contain input fields for login'
            r.status == 200
            r.headers['Content-Type'][0].contains 'text/html'
            r.body.str =~ /<input[^>]*name="j_username"/
            r.body.str =~ /<input[^>]*name="j_password"/

        when: 'post form with valid credentials'
            r = POST '/login.do',
                ContentType: 'application/x-www-form-urlencoded',
                Cookie: cookie1,
                body: [j_username: 'tomy', j_password: 'best']

        then: 'should be redirected back to the authorization endpoint with the same query as in the first step'
            def location2 = r.headers['Location'][0]
            def cookie2 = r.headers['Set-Cookie'] ? parseCookie(r.headers) : cookie1
        and:
            r.status == 302
            location2 == "${serverUri}/oauth/authorize?${formatQuery(authzParams)}"

        when: 'follow redirection back to the authorization endpoint'
            r = GET location2,
                Cookie: cookie2

        then: 'should get HTML page that contains user_oauth_approval'
            r.status == 200
            r.headers['Content-Type'][0].contains 'text/html'
            r.body.str.contains USER_OAUTH_APPROVAL

        when: 'post form with authorization approval'
            r = POST '/oauth/authorize',
                ContentType: 'application/x-www-form-urlencoded',
                Cookie: cookie2,
                body: [(USER_OAUTH_APPROVAL): 'true']

        then: 'should be redirected to the specified redirect_uri with authorization code'
            r.status == 302
            r.headers['Location'][0] ==~ /${redirectUri}\?code=.*&state=xyz/
        and:
            def matcher = r.headers['Location'][0] =~ /code=(.*)&/
            def code = matcher[0][1]

        when: 'send authorized request to the token endpoint'
            r = POST '/oauth/token',
                ContentType: 'application/x-www-form-urlencoded',
                Accept: 'application/json',
                Authorization: "Basic ${base64(credentials)}",
                body: [code: code, grant_type: 'authorization_code', redirect_uri: redirectUri]

        then: 'should get success response with JSON'
            r.status == 200
            r.headers['Content-Type'][0].contains 'application/json'
            r.body.json['token_type'] == 'bearer'
            r.body.json['expires_in'] > 0

        and: 'access and refresh token is valid'
            assertAccessToken r.body.json['access_token'] as String
            assertRefreshToken r.body.json['refresh_token'] as String

        and: 'scope contains only the requested scope'
            r.body.json['scope'] == client.scope[0]
    }

    def 'request user authorization and reject access'() {
        setup:
            dropCollection PersistableAccessToken
        and:
            def redirectUri = client.registeredRedirectUri[1]
            def authzParams = [response_type: 'code', state: 'zyx',
                               client_id: client.clientId, redirect_uri: redirectUri]
            def location, cookie

        and: 'send request to the authorization endpoint'
            r = GET '/oauth/authorize', query: authzParams
            location = r.headers['Location'][0]
            cookie = parseCookie(r.headers)
            r = GET location, Cookie: cookie

        and: 'post form with valid credentials'
            r = POST '/login.do',
                ContentType: 'application/x-www-form-urlencoded',
                Cookie: cookie,
                body: [j_username: 'tomy', j_password: 'best']
            location = r.headers['Location'][0]
            cookie = r.headers['Set-Cookie'] ? parseCookie(r.headers) : cookie
            r = GET location, Cookie: cookie

        when: 'post form with authorization rejection'
            r = POST '/oauth/authorize',
                ContentType: 'application/x-www-form-urlencoded',
                Cookie: cookie,
                body: [(USER_OAUTH_APPROVAL): 'false']

        then: 'should be redirected to the specified redirect_uri with error access_denied'
            r.status == 302
            r.headers['Location'][0].with {
                it.startsWith(redirectUri) &&
                it.contains('error=access_denied') &&
                it.contains('state=zyx') &&
                ! it.contains('code=')
            }
    }
}
