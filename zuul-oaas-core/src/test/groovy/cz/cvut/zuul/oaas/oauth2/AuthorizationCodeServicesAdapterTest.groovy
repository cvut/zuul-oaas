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
package cz.cvut.zuul.oaas.oauth2

import cz.cvut.zuul.oaas.models.PersistableAuthorizationCode
import cz.cvut.zuul.oaas.repos.AuthorizationCodesRepo
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import spock.lang.Specification

class AuthorizationCodeServicesAdapterTest extends Specification {

    @Delegate
    CoreObjectFactory factory = new CoreObjectFactory()

    def auth = build(OAuth2Authentication)
    def repo = Mock(AuthorizationCodesRepo)
    def service = new AuthorizationCodeServicesAdapter(repo)


    def "store: creates PersistableAuthorizationCode and saves it to the repo"() {
        when:
            service.store('abcd', auth)
        then:
            1 * repo.save({ PersistableAuthorizationCode it ->
                it.code == 'abcd' && it.authentication == auth
            })
    }

    def "remove: finds authentication in the repo by the code, deletes and returns it"() {
        when:
            def result = service.remove('abcd')
        then:
            1 * repo.findOne('abcd') >> new PersistableAuthorizationCode('abcd', auth)
        then:
            1 * repo.deleteById('abcd')
            result == auth
    }

    def "remove: returns null when the code doesn't exist in the repo"() {
        when:
            def result = service.remove('unknown')
        then:
            1 * repo.findOne('unknown') >> null
            result == null
    }
}
