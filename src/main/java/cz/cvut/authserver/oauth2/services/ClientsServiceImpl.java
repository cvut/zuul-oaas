package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.generators.OAuth2ClientCredentialsGenerator;
import java.util.LinkedHashSet;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

/**
 *
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ClientsServiceImpl implements ClientsService {

    private ClientDetailsService clientDetailsService;
    
    private ClientRegistrationService clientRegistrationService;
    
    private OAuth2ClientCredentialsGenerator oauth2ClientCredentialsGenerator;
    
    
    //////////  Business methods  //////////

    @Override
    public ClientDetails findClientDetailsById(String clientId) throws OAuth2Exception {
        return clientDetailsService.loadClientByClientId(clientId);
    }

    @Override
    public void createClientDetails(BaseClientDetails client) throws Exception {
       
        // generate oauth2 client credentials
        String clientId = oauth2ClientCredentialsGenerator.generateClientId();
        String clientSecret = oauth2ClientCredentialsGenerator.generateClientSecret();

        // setting necessary fields
        client.setClientId(clientId);
        client.setClientSecret(clientSecret);
        client.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_CLIENT"));

        // save
        clientRegistrationService.addClientDetails(client);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        clientRegistrationService.removeClientDetails(clientId);
    }

    @Override
    public void resetClientSecret(String clientId) throws NoSuchClientException {
        String newSecret = oauth2ClientCredentialsGenerator.generateClientSecret();
        clientRegistrationService.updateClientSecret(clientId, newSecret);
    }

    @Override
    public void addScopeToClientDetails(String clientId, String scope) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        BaseClientDetails baseClientDetails = (BaseClientDetails) clientDetails;
        baseClientDetails.setScope(new LinkedHashSet<String>());
        baseClientDetails.getScope().add(scope);
        clientDetails = baseClientDetails;
        clientRegistrationService.updateClientDetails(clientDetails);
    }

    @Override
    public void removeScopeFromClientDetails(String clientId, String scope) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        if (clientDetails.getScope().contains(scope)) {
            clientDetails.getScope().remove(scope);
            clientRegistrationService.updateClientDetails(clientDetails);
        }
    }

    @Override
    public void addGrantToClientDetails(String clientId, String grantType) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        clientDetails.getScope().add(grantType);
        clientRegistrationService.updateClientDetails(clientDetails);
    }

    @Override
    public void deleteGrantFromClientDetails(String clientId, String grantType) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        if (clientDetails.getAuthorizedGrantTypes().contains(grantType)) {
            clientDetails.getAuthorizedGrantTypes().remove(grantType);
            clientRegistrationService.updateClientDetails(clientDetails);
        }
    }

    @Override
    public void addRoleToClientDetails(String clientId, String role) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        clientDetails.getAuthorities().addAll(AuthorityUtils.createAuthorityList(role));
        clientRegistrationService.updateClientDetails(clientDetails);
    }

    @Override
    public void deleteRoleFromClientDetails(String clientId, String role) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        GrantedAuthority removed = new SimpleGrantedAuthority(role);
        if (clientDetails.getAuthorities().contains(removed)) {
            clientDetails.getAuthorities().remove(removed);
            clientRegistrationService.updateClientDetails(clientDetails);
        }
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
