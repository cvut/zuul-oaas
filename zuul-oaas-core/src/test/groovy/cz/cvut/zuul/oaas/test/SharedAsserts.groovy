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
package cz.cvut.zuul.oaas.test

import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

class SharedAsserts {

    static isEqual(OAuth2Authentication actual, OAuth2Authentication expected) {
        def actualOAuthReq = actual.getOAuth2Request()
        def expectOAuthReq = expected.getOAuth2Request()
        def actualUserAuth = actual.userAuthentication
        def expectUserAuth = expected.userAuthentication

        assertThat (actual) equalsTo expected inAllPropertiesExcept ('userAuthentication', 'OAuth2Request', 'authorities')
        actual.authorities as Set == expected.authorities as Set

        assertThat (actualOAuthReq) equalsTo (expectOAuthReq) inAllPropertiesExcept ('authorities')
        actualOAuthReq.authorities as Set == expectOAuthReq.authorities as Set

        if (expectUserAuth) {
            assertThat (actualUserAuth) equalsTo (expectUserAuth) inAllPropertiesExcept ('authorities')
            assert actualUserAuth.authorities as Set == expectUserAuth.authorities as Set
        }
    }
}
