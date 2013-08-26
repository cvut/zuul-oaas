package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.dao.AccessTokenDAO;
import cz.cvut.zuul.oaas.dao.ClientDAO;
import cz.cvut.zuul.oaas.dao.RefreshTokenDAO;
import cz.cvut.zuul.oaas.generators.OAuth2ClientCredentialsGenerator;
import cz.cvut.zuul.oaas.models.Client;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.NONE;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Service
@Setter @Slf4j
public class ClientsServiceImpl implements ClientsService {

    private static final List<GrantedAuthority> DEFAULT_AUTHORITIES =  AuthorityUtils.createAuthorityList("ROLE_CLIENT");

    private ClientDAO clientDAO;
    private AccessTokenDAO accessTokenDAO;
    private RefreshTokenDAO refreshTokenDAO;

    private @Setter(NONE) MapperFacade mapper;
    private OAuth2ClientCredentialsGenerator credentialsGenerator;


    //////////  Business methods  //////////

    @Override
    public ClientDTO findClientById(String clientId) throws NoSuchClientException {
        Client client = clientDAO.findOne(clientId);

        if (client == null) {
            throw new NoSuchClientException(String.format("Client with id [%s] doesn't exists.", clientId));
        }
        return mapper.map(client, ClientDTO.class);
    }

    @Override
    public String createClient(ClientDTO clientDTO) throws ClientAlreadyExistsException {
        Client client = mapper.map(clientDTO, Client.class);

        String clientId;
        do {
            log.debug("Generating a new clientId");
            clientId = credentialsGenerator.generateClientId();
        } while (clientDAO.exists(clientId));

        String clientSecret = credentialsGenerator.generateClientSecret();

        client.setClientId(clientId);
        client.setClientSecret(clientSecret);

        if (isEmpty(client.getAuthorities())) {
            client.setAuthorities(DEFAULT_AUTHORITIES);
        }

        log.info("Saving a new client: [{}]", client);
        clientDAO.save(client);

        return clientId;
    }

    @Override
    public void updateClient(ClientDTO clientDTO) throws NoSuchClientException {
        log.info("Updating client: [{}]", clientDTO);

        assertClientExists(clientDTO.getClientId());
        clientDAO.save(mapper.map(clientDTO, Client.class));
    }

    @Override
    public void removeClient(String clientId) throws NoSuchClientException {
        log.info("Removing client: [{}]", clientId);
        assertClientExists(clientId);

        //remove associated tokens
        accessTokenDAO.deleteByClientId(clientId);
        refreshTokenDAO.deleteByClientId(clientId);

        clientDAO.delete(clientId);
    }

    @Override
    public void resetClientSecret(String clientId) throws NoSuchClientException {
        log.info("Resetting secret for client: [{}]", clientId);

        String newSecret = credentialsGenerator.generateClientSecret();
        try {
            clientDAO.updateClientSecret(clientId, newSecret);

        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException(ex.getMessage(), ex);
        }
    }

    private void assertClientExists(String clientId) {
        if (! clientDAO.exists(clientId)) {
            throw new NoSuchClientException("No such client with id = " + clientId);
        }
    }


    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapper = mapperFacade;
    }
}
