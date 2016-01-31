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
package cz.cvut.zuul.oaas.persistence.support

import cz.cvut.zuul.oaas.test.CoreObjectFactory
import cz.cvut.zuul.oaas.test.ObjectFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import spock.lang.Specification

class JsonOAuth2AuthenticationConverterTest extends Specification {

    @Delegate
    @SuppressWarnings('GroovyUnusedDeclaration')
    private final ObjectFactory factory = new CoreObjectFactory()

    def converter = new JsonOAuth2AuthenticationConverter()


    def 'serialize and deserialize, the result should be equal to original'() {
        setup:
            def expected = build(OAuth2Authentication, [clientOnly: false])
        when:
            def actual = converter.deserialize(converter.serialize(expected))
        then:
            actual.OAuth2Request == expected.OAuth2Request
            actual.userAuthentication.principal == expected.userAuthentication.principal
            actual.userAuthentication.authorities as Set == expected.userAuthentication.authorities as Set
    }
}
