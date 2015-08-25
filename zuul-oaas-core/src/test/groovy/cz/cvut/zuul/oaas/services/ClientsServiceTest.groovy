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
package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.api.exceptions.NoSuchClientException
import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.keygen.StringKeyGenerator
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

@Mixin(CoreObjectFactory)
class ClientsServiceTest extends Specification {

    def clientsRepo = Mock(ClientsRepo)
    def accessTokensRepo = Mock(AccessTokensRepo)
    def refreshTokensRepo = Mock(RefreshTokensRepo)
    def clientIdGenerator = Mock(StringKeyGenerator)
    def secretGenerator = Mock(StringKeyGenerator)

    def service = new ClientsServiceImpl(
            clientsRepo: clientsRepo,
            accessTokensRepo: accessTokensRepo,
            refreshTokensRepo: refreshTokensRepo,
            clientIdGenerator: clientIdGenerator,
            secretGenerator: secretGenerator
    )

    def setup() {
        service.setupMapper()
    }


    def 'find client by non existing id'() {
        when:
            service.findClientById('non-existing')
        then:
            clientsRepo.findOne('non-existing') >> null
            thrown(NoSuchClientException)
    }


    def 'create client'() {
        setup:
            def client = build(ClientDTO).with {
                clientId = 'irrelevant'; clientSecret = 'whatever'; return it
            }
            def generatedId = 'client-123'
        when:
            def returnedId = service.createClient(client)
        then:
            1 * clientIdGenerator.generateKey() >> generatedId
            1 * secretGenerator.generateKey() >> 'top-secret'

            1 * clientsRepo.save({ Client it ->
                it.clientId == generatedId
                it.clientSecret == 'top-secret'
            })
            returnedId == generatedId
    }

    def 'create client with default authorities'() {
        setup:
            def client = build(ClientDTO).with {
                it.authorities = []; return it
            }
            clientIdGenerator.generateKey() >> 'top-secret'
        when:
            service.createClient(client)
        then:
            1 * clientsRepo.save( { ! it.authorities?.isEmpty() } )
    }

    def 'create client and handle generation of already taken id'() {
        setup:
            def client = build(ClientDTO)
            def generatedId = 'client-123'
        when:
            service.createClient(client) == generatedId

        then: 'generate unique id at the third attempt'
            3 * clientIdGenerator.generateKey() >>> ['taken-id', 'still-bad', generatedId]
            3 * clientsRepo.exists(_) >>> [true, true, false]
        then:
            1 * clientsRepo.save({ Client it ->
                it.clientId == generatedId
            })
    }


    def 'update existing client given empty secret'() {
        setup:
            clientsRepo.exists(_) >> true
        when:
            service.updateClient(client)
        then:
            1 * secretGenerator.generateKey() >> 'new-secret'

            1 * clientsRepo.save({ Client it ->
                it.clientSecret == 'new-secret'
            })
        where:
            client = build(ClientDTO).with { it.clientSecret = ''; it }
    }

    def 'update existing client given empty authorities'() {
        setup:
            clientsRepo.exists(_) >> true
        when:
            service.updateClient(client)
        then:
            1 * clientsRepo.save({ Client it ->
                ! it.authorities?.isEmpty()
            })
        where:
            client = build(ClientDTO).with { it.authorities = []; it }
    }

    def 'update non existing client'() {
        when:
            service.updateClient( build(ClientDTO) )
        then:
            clientsRepo.exists(_) >> false
            thrown(NoSuchClientException)
    }


    def 'remove existing client'() {
        setup:
            def clientId = 'client-123'
            clientsRepo.exists(clientId) >> true
        when:
            service.removeClient(clientId)
        then:
            1 * clientsRepo.deleteById(clientId)
            1 * accessTokensRepo.deleteByClientId(clientId)
            1 * refreshTokensRepo.deleteByClientId(clientId)
    }

    def 'remove non existing client'() {
        when:
            service.removeClient('non-existing')
        then:
            clientsRepo.exists('non-existing') >> false
            thrown(NoSuchClientException)
    }


    def 'reset client secret for existing client'() {
        when:
            service.resetClientSecret('client-123')
        then:
            1 * secretGenerator.generateKey() >> 'top-secret'
            1 * clientsRepo.updateClientSecret('client-123', 'top-secret')
    }

    def 'reset client secret for non existing client'() {
        when:
           service.resetClientSecret('non-existing')
        then:
            1 * clientsRepo.updateClientSecret('non-existing', _) >> {
                throw new EmptyResultDataAccessException(1)
            }
            thrown(NoSuchClientException)
    }


    def 'map Client to ClientDTO'() {
        given:
            def client = build(Client)
        when:
            def dto = service.mapper.map(client, ClientDTO)
        then:
            assertMapping client, dto
    }

    def 'map ClientDTO to Client'() {
        given:
            def dto = build(ClientDTO)
        when:
            def client = service.mapper.map(dto, Client)
        then:
            assertMapping client, dto
    }

    private void assertMapping(Client entity, ClientDTO dto) {

        assertThat( entity ).equalsTo( dto ).inAllPropertiesExcept(
            'authorities', 'accessTokenValidity', 'refreshTokenValidity')

        def entityAuthorities = entity.authorities ? entity.authorities*.toString() : []
        assert entity.accessTokenValiditySeconds == dto.accessTokenValidity
        assert entity.refreshTokenValiditySeconds == dto.refreshTokenValidity
        assert entityAuthorities as Set == dto.authorities as Set
    }
}
