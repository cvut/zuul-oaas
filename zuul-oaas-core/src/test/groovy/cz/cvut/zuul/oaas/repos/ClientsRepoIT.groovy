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
package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.models.Client
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException

abstract class ClientsRepoIT extends BaseRepositoryIT<Client> {

    @Autowired ClientsRepo repo


    def 'update secret for non existing client'() {
        when:
            repo.updateClientSecret('unknown-id', 'new-secret')
        then:
            thrown(EmptyResultDataAccessException)

    }

    def 'update client secret'() {
        setup:
            def client = build(Client)
            assert client.clientSecret != 'new-secret'
            repo.save(client)
        when:
            repo.updateClientSecret(client.clientId, 'new-secret')
        then:
            repo.findOne(client.clientId).clientSecret == 'new-secret'
    }

    def 'update redirect URIs'() {
        setup:
            def client = build(Client, [registeredRedirectUri: ['http://example.org']])
            repo.save(client)
        when:
            def expected = repo.findOne(client.clientId)
            expected.registeredRedirectUri << 'https://cool.org'
            repo.save(expected)
        then:
            def actual = repo.findOne(client.clientId)
            actual.registeredRedirectUri == expected.registeredRedirectUri
    }
}
