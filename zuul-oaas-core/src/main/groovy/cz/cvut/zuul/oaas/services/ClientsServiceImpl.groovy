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
package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.api.exceptions.NoSuchClientException
import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.api.services.ClientsService
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo
import cz.cvut.zuul.oaas.services.converters.GrantedAuthorityConverter
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.keygen.StringKeyGenerator
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

import static cz.cvut.zuul.oaas.common.Looper.loop
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList

@Service @Slf4j
class ClientsServiceImpl implements ClientsService {

    private static final DEFAULT_AUTHORITIES = createAuthorityList('ROLE_CLIENT')

    ClientsRepo clientsRepo
    AccessTokensRepo accessTokensRepo
    RefreshTokensRepo refreshTokensRepo

    StringKeyGenerator clientIdGenerator
    StringKeyGenerator secretGenerator

    /**
     * Orika Mapper Factory to be configured and used for mapping between entity
     * and DTO objects. If no factory is provided, then new one will be created.
     *
     * @see {@link #setupMapper()}
     */
    private MapperFactory mapperFactory

    @PackageScope MapperFacade mapper



    ClientDTO findClientById(String clientId) {

        def client = clientsRepo.findOne(clientId)
        if (!client) {
            throw new NoSuchClientException("Client with id [${clientId}] doesn't exists.")
        }
        mapper.map(client, ClientDTO)
    }

    String createClient(ClientDTO clientDTO) {

        def client = mapper.map(clientDTO, Client)

        client.clientId = loop {
            log.debug 'Generating unique clientId...'
            clientIdGenerator.generateKey()
        } until { id, i -> !clientsRepo.exists(id) || i > 10 }

        if (!client.clientId) {
            throw new IllegalStateException('Failed to generate client id')
        }

        client.clientSecret = secretGenerator.generateKey()
        client.authorities = client.authorities ?: DEFAULT_AUTHORITIES

        log.info 'Saving a new client: [{}]', client
        clientsRepo.save(client)

        client.clientId
    }

    void updateClient(ClientDTO clientDTO) {
        log.info 'Updating client: [{}]', clientDTO

        assertClientExists clientDTO.clientId

        def client = mapper.map(clientDTO, Client)

        if (!client.clientSecret?.trim()) {
            log.info 'Resetting secret for client: [{}]', client.clientId
            client.clientSecret = secretGenerator.generateKey()
        }
        client.authorities = client.authorities ?: DEFAULT_AUTHORITIES

        clientsRepo.save(client)
    }

    void removeClient(String clientId) {
        log.info 'Removing client: [{}]', clientId

        assertClientExists clientId

        //remove associated tokens
        accessTokensRepo.deleteByClientId(clientId)
        refreshTokensRepo.deleteByClientId(clientId)

        clientsRepo.deleteById(clientId)
    }

    void resetClientSecret(String clientId) {
        log.info 'Resetting secret for client: [{}]', clientId

        def newSecret = secretGenerator.generateKey()

        try {
            clientsRepo.updateClientSecret(clientId, newSecret)

        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException(ex.message, ex)
        }
    }


    private assertClientExists(String clientId) {
        if (!clientsRepo.exists(clientId)) {
            throw new NoSuchClientException("No such client with id = ${clientId}")
        }
    }

    @PostConstruct setupMapper() {
        def factory = mapperFactory ?: new Builder().build()

        factory.converterFactory
               .registerConverter(new GrantedAuthorityConverter())

        factory.classMap(Client, ClientDTO)
                .field('registeredRedirectUri', 'redirectUris')
                .field('accessTokenValiditySeconds', 'accessTokenValidity')
                .field('refreshTokenValiditySeconds', 'refreshTokenValidity')
                .exclude('metaClass')
                .byDefault().register()

        mapper = factory.mapperFacade
    }
}
