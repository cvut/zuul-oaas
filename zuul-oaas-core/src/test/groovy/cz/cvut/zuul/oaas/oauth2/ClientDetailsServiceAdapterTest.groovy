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

import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.security.oauth2.common.exceptions.InvalidClientException
import spock.lang.Specification

@Mixin(CoreObjectFactory)
class ClientDetailsServiceAdapterTest extends Specification {

    def repo = Mock(ClientsRepo)
    def service = new ClientDetailsServiceAdapter(repo)


    def "loadClientByClientId: returns client from the repository"() {
        given:
            def expected = build(Client)
            def clientId = expected.clientId
        when:
            def actual = service.loadClientByClientId(clientId)
        then:
            1 * repo.findOne(clientId) >> expected
            actual == expected
    }

    def "loadClientByClientId: throws InvalidClientException when no client is found"() {
        setup:
            repo.findOne(_) >> null
        when:
            service.loadClientByClientId('foo')
        then:
            thrown InvalidClientException
    }
}
