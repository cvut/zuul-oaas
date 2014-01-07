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
package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.exceptions.NoSuchClientException;
import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.api.services.ClientsService;
import cz.cvut.zuul.oaas.models.Client;
import cz.cvut.zuul.oaas.repos.AccessTokensRepo;
import cz.cvut.zuul.oaas.repos.ClientsRepo;
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo;
import cz.cvut.zuul.oaas.services.converters.GrantedAuthorityConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PACKAGE;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Setter @Slf4j
public class ClientsServiceImpl implements ClientsService {

    private static final List<GrantedAuthority> DEFAULT_AUTHORITIES =  AuthorityUtils.createAuthorityList("ROLE_CLIENT");

    private ClientsRepo clientsRepo;
    private AccessTokensRepo accessTokensRepo;
    private RefreshTokensRepo refreshTokensRepo;

    private StringKeyGenerator clientIdGenerator;
    private StringKeyGenerator secretGenerator;

    /**
     * Orika Mapper Factory to be configured and used for mapping between entity
     * and DTO objects. If no factory is provided, then new one will be created.
     *
     * @see {@link #setupMapper()}
     */
    private MapperFactory mapperFactory;

    @Setter(NONE) @Getter(PACKAGE)
    private MapperFacade mapper;



    public ClientDTO findClientById(String clientId) {
        Client client = clientsRepo.findOne(clientId);

        if (client == null) {
            throw new NoSuchClientException("Client with id [%s] doesn't exists.", clientId);
        }
        return mapper.map(client, ClientDTO.class);
    }

    public String createClient(ClientDTO clientDTO) {
        Client client = mapper.map(clientDTO, Client.class);

        String clientId;
        do {
            log.debug("Generating unique clientId...");
            clientId = clientIdGenerator.generateKey();
        } while (clientsRepo.exists(clientId));

        String plainSecret = secretGenerator.generateKey();

        client.setClientId(clientId);
        client.setClientSecret(plainSecret);

        if (isEmpty(client.getAuthorities())) {
            client.setAuthorities(DEFAULT_AUTHORITIES);
        }

        log.info("Saving a new client: [{}]", client);
        clientsRepo.save(client);

        return clientId;
    }

    public void updateClient(ClientDTO clientDTO) {
        log.info("Updating client: [{}]", clientDTO);
        assertClientExists(clientDTO.getClientId());

        Client client = mapper.map(clientDTO, Client.class);

        if (isBlank(client.getClientSecret())) {
            log.info("Resetting secret for client: [{}]", client.getClientId());
            client.setClientSecret(secretGenerator.generateKey());
        }
        if (isEmpty(client.getAuthorities())) {
            client.setAuthorities(DEFAULT_AUTHORITIES);
        }
        clientsRepo.save(client);
    }

    public void removeClient(String clientId) {
        log.info("Removing client: [{}]", clientId);
        assertClientExists(clientId);

        //remove associated tokens
        accessTokensRepo.deleteByClientId(clientId);
        refreshTokensRepo.deleteByClientId(clientId);

        clientsRepo.delete(clientId);
    }

    public void resetClientSecret(String clientId) {
        log.info("Resetting secret for client: [{}]", clientId);

        String newSecret = secretGenerator.generateKey();

        try {
            clientsRepo.updateClientSecret(clientId, newSecret);

        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException(ex.getMessage(), ex);
        }
    }


    private void assertClientExists(String clientId) {
        if (! clientsRepo.exists(clientId)) {
            throw new NoSuchClientException("No such client with id = " + clientId);
        }
    }

    @PostConstruct void setupMapper() {
        MapperFactory factory = defaultIfNull(mapperFactory, new Builder().build());

        factory.getConverterFactory()
                .registerConverter(new GrantedAuthorityConverter());

        factory.classMap(Client.class, ClientDTO.class)
                .byDefault().register();

        mapper = factory.getMapperFacade();
    }
}
