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
package cz.cvut.zuul.oaas.test

import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

class SharedAsserts {

    static isEqual(OAuth2Authentication actual, OAuth2Authentication expected) {
        def actualAuthzReq = actual.authorizationRequest
        def expectAuthzReq = expected.authorizationRequest
        def actualUserAuth = actual.userAuthentication
        def expectUserAuth = expected.userAuthentication

        assertThat (actual) equalsTo expected inAllPropertiesExcept ('userAuthentication', 'authorizationRequest', 'authorities')

        actual.authorities as Set == expected.authorities as Set

        assertThat (actualAuthzReq) equalsTo (expectAuthzReq) inAllPropertiesExcept ('authorizationParameters')

        // scope parameter contains space separated scopes extracted from property of type Set,
        // thus it can be shuffled so we must exclude it from assertion
        def actualAuthzParams = actualAuthzReq.authorizationParameters.findAll { it.key != 'scope' }
        def expectedAuthzParams =  expectAuthzReq.authorizationParameters.findAll { it.key != 'scope' }
        actualAuthzParams == expectedAuthzParams

        if (expectUserAuth) {
            assertThat (actualUserAuth) equalsTo (expectUserAuth) inAllPropertiesExcept ('authorities')
            assert actualUserAuth.authorities as Set == expectUserAuth.authorities as Set
        }
    }
}
