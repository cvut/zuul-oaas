package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import cz.cvut.authserver.oauth2.dao.ClientDAO;
import cz.cvut.authserver.oauth2.generators.OAuth2ClientCredentialsGenerator;
import cz.cvut.authserver.oauth2.models.Client;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 *
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ClientsServiceImpl implements ClientsService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientsServiceImpl.class);
    private static final List<GrantedAuthority> DEFAULT_AUTHORITIES =  AuthorityUtils.createAuthorityList("ROLE_CLIENT");

    private ClientDAO clientDAO;

    private MapperFacade mapper;
    
    private OAuth2ClientCredentialsGenerator oauth2ClientCredentialsGenerator;
    
    
    //////////  Business methods  //////////

    @Override
    public ClientDTO findClientById(String clientId) throws NoSuchClientException, OAuth2Exception {
        Client client = clientDAO.findOne(clientId);

        if (client == null) {
            throw new NoSuchClientException(String.format("Client with id [%s] doesn't exists.", clientId));
        }
        return mapper.map(client, ClientDTO.class);
    }

    @Override
    public String createClient(ClientDTO clientDTO) throws ClientAlreadyExistsException {
        LOG.info("Creating new client: [{}]", clientDTO);

        Client client = mapper.map(clientDTO, Client.class);

        // generate oauth2 client credentials
        String clientId = oauth2ClientCredentialsGenerator.generateClientId();
        String clientSecret = oauth2ClientCredentialsGenerator.generateClientSecret();
        
        // set necessary fields
        client.setClientId(clientId);
        client.setClientSecret(clientSecret);

        if (isEmpty(client.getAuthorities())) {
            client.setAuthorities(DEFAULT_AUTHORITIES);
        } else {
            client.setAuthorities(client.getAuthorities());
        }
        clientDAO.save(client);

        return clientId;
    }

    @Override
    public void updateClient(ClientDTO clientDTO) throws NoSuchClientException {
        LOG.info("Updating client: [{}]", clientDTO);
        try {
            clientDAO.update(mapper.map(clientDTO, Client.class));

        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException(ex.getMessage(), ex);
        }
    }

    @Override
    public void removeClient(String clientId) throws NoSuchClientException {
        LOG.info("Removing client: [{}]", clientId);
        assertClientExists(clientId);

        clientDAO.delete(clientId);
    }

    @Override
    public void resetClientSecret(String clientId) throws NoSuchClientException {
        LOG.info("Resetting secret for client: [{}]", clientId);

        String newSecret = oauth2ClientCredentialsGenerator.generateClientSecret();
        clientDAO.updateClientSecret(clientId, newSecret);
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

    public void setOauth2ClientCredentialsGenerator(OAuth2ClientCredentialsGenerator oauth2ClientCredentialsGenerator) {
        this.oauth2ClientCredentialsGenerator = oauth2ClientCredentialsGenerator;
    }

    public void setMapperFacade(MapperFacade mapper) {
        this.mapper = mapper;
    }
}
