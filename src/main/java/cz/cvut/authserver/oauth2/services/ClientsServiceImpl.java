package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import cz.cvut.authserver.oauth2.generators.OAuth2ClientCredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
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

    private ClientDetailsService clientDetailsService;

    private ClientRegistrationService clientRegistrationService;
    
    private OAuth2ClientCredentialsGenerator oauth2ClientCredentialsGenerator;
    
    
    //////////  Business methods  //////////

    @Override
    public ClientDTO findClientDetailsById(String clientId) throws NoSuchClientException, OAuth2Exception {
        try {
            return new ClientDTO(clientDetailsService.loadClientByClientId(clientId));

        } catch (OAuth2Exception ex) {
            // loadClientByClientId throws OAuth2Exception "invalid_client" when no client exists with the given id,
            // resulting in 401 Unauthorized Status code which doesn't fit our purposes at all ...
            if (OAuth2Exception.INVALID_CLIENT.equals(ex.getOAuth2ErrorCode())) {
                // this will result in 404 Not Found
                throw new NoSuchClientException(String.format("Client with id [%s] doesn't exists.", clientId), ex);
            }
            throw ex;
        }
    }

    @Override
    public String createClientDetails(ClientDTO client) throws ClientAlreadyExistsException {
        LOG.info("Creating new client: [{}]", client);

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
        clientRegistrationService.addClientDetails(client);  //save

        return clientId;
    }

    @Override
    public void updateClientDetails(ClientDTO client) throws NoSuchClientException {
        LOG.info("Updating client: [{}]", client);
        clientRegistrationService.updateClientDetails(client);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        LOG.info("Removing client: [{}]", clientId);
        clientRegistrationService.removeClientDetails(clientId);
    }

    @Override
    public void resetClientSecret(String clientId) throws NoSuchClientException {
        LOG.info("Reseting secret for client: [{}]", clientId);

        String newSecret = oauth2ClientCredentialsGenerator.generateClientSecret();
        clientRegistrationService.updateClientSecret(clientId, newSecret);
    }

    
    //////////  Getters / Setters  //////////

    public ClientDetailsService getClientDetailsService() {
        return clientDetailsService;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    public ClientRegistrationService getClientRegistrationService() {
        return clientRegistrationService;
    }

    public void setClientRegistrationService(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    public OAuth2ClientCredentialsGenerator getOauth2ClientCredentialsGenerator() {
        return oauth2ClientCredentialsGenerator;
    }

    public void setOauth2ClientCredentialsGenerator(OAuth2ClientCredentialsGenerator oauth2ClientCredentialsGenerator) {
        this.oauth2ClientCredentialsGenerator = oauth2ClientCredentialsGenerator;
    }
}
