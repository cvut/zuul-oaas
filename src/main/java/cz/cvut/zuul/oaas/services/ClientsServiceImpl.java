package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.dao.AccessTokenDAO;
import cz.cvut.zuul.oaas.dao.ClientDAO;
import cz.cvut.zuul.oaas.dao.RefreshTokenDAO;
import cz.cvut.zuul.oaas.generators.OAuth2ClientCredentialsGenerator;
import cz.cvut.zuul.oaas.models.Client;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ClientsServiceImpl implements ClientsService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientsServiceImpl.class);
    private static final List<GrantedAuthority> DEFAULT_AUTHORITIES =  AuthorityUtils.createAuthorityList("ROLE_CLIENT");

    private ClientDAO clientDAO;
    private AccessTokenDAO accessTokenDAO;
    private RefreshTokenDAO refreshTokenDAO;

    private MapperFacade mapper;
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
            LOG.debug("Generating a new clientId");
            clientId = credentialsGenerator.generateClientId();
        } while (clientDAO.exists(clientId));

        String clientSecret = credentialsGenerator.generateClientSecret();
        
        client.setClientId(clientId);
        client.setClientSecret(clientSecret);

        if (isEmpty(client.getAuthorities())) {
            client.setAuthorities(DEFAULT_AUTHORITIES);
        }

        LOG.info("Saving a new client: [{}]", client);
        clientDAO.save(client);

        return clientId;
    }

    @Override
    public void updateClient(ClientDTO clientDTO) throws NoSuchClientException {
        LOG.info("Updating client: [{}]", clientDTO);

        assertClientExists(clientDTO.getClientId());
        clientDAO.save(mapper.map(clientDTO, Client.class));
    }

    @Override
    public void removeClient(String clientId) throws NoSuchClientException {
        LOG.info("Removing client: [{}]", clientId);
        assertClientExists(clientId);
        
        //remove associated tokens
        accessTokenDAO.deleteByClientId(clientId);
        refreshTokenDAO.deleteByClientId(clientId);

        clientDAO.delete(clientId);
    }

    @Override
    public void resetClientSecret(String clientId) throws NoSuchClientException {
        LOG.info("Resetting secret for client: [{}]", clientId);

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

    
    //////////  Getters / Setters  //////////

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.accessTokenDAO = accessTokenDAO;
    }

    public void setRefreshTokenDAO(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO;
    }

    public void setCredentialsGenerator(OAuth2ClientCredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    public void setMapperFacade(MapperFacade mapper) {
        this.mapper = mapper;
    }
}
