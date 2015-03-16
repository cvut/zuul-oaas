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
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.common.exceptions.InvalidClientException
import spock.lang.Specification

@Mixin(CoreObjectFactory)
class LockableClientUserDetailsServiceTest extends Specification {

    def repo = Mock(ClientsRepo)
    def passwordEncoder = Mock(PasswordEncoder)

    def service = new LockableClientUserDetailsService(clientsRepo: repo)


    def "loadUserByUsername: returns Spring's User object populated from Client loaded from the repository"() {
        given:
            def client = build(Client)
        when:
            def user = service.loadUserByUsername(client.clientId)
        then:
            1 * repo.findOne(client.clientId) >> client
        and:
            user instanceof User
            user.username == client.clientId
            user.password == client.clientSecret
            user.accountNonLocked == !client.locked
            user.authorities == client.authorities
            user.enabled
            user.accountNonExpired
            user.credentialsNonExpired
    }

    def "loadUserByUsername: uses empty password encoded via the passwordEncoder when clientSecret is empty"() {
        setup:
            passwordEncoder.encode('') >> 'hashed'
            repo.findOne('foo') >> new Client(clientId: 'foo', clientSecret: secret)
        and:
            service.passwordEncoder = passwordEncoder
        when:
            def result = service.loadUserByUsername('foo')
        then:
            result.password == 'hashed'
        where:
            secret << [null, '']
    }

    def "loadUserByUsername: throws InvalidClientException when no client is found"() {
        setup:
            repo.findOne(_) >> null
        when:
            service.loadUserByUsername('foo')
        then:
            thrown InvalidClientException
    }
}
