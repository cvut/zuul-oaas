package cz.cvut.authserver.oauth2.services;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface ClientsService {
    
    ClientDetails findClientDetailsById(@PathVariable String clientId) throws OAuth2Exception;

    void createClientDetails(BaseClientDetails client) throws ClientAlreadyExistsException;
    
    void removeClientDetails(String clientId) throws NoSuchClientException;
    
    void resetClientSecret(String clientId) throws NoSuchClientException;
    
    void addResourceToClientDetails(String clientId, @RequestBody String resourceId) throws Exception;
    
    void removeResourceFromClientDetails(String clientId, String resourceId) throws OAuth2Exception, NoSuchClientException;
    
    void addGrantToClientDetails(String clientId, String grantType) throws Exception;

    void deleteGrantFromClientDetails(String clientId, String grantType) throws OAuth2Exception, NoSuchClientException;

    void addScopeToClientDetails(String clientId, @RequestBody String scope) throws Exception;
    
    void removeScopeFromClientDetails(String clientId, String scope) throws OAuth2Exception, NoSuchClientException;

    void addRoleToClientDetails(String clientId, String role) throws Exception;

    void deleteRoleFromClientDetails(String clientId, String role) throws OAuth2Exception, NoSuchClientException;

    void addRedirectUriToClientDetails(String clientId, String redirectUri) throws Exception;

    void deleteRedirectUriFromClientDetails(String clientId, String redirectUri) throws OAuth2Exception, NoSuchClientException;
    
    void addBrandingInformationToClientDetails(String clientId, String brand) throws OAuth2Exception, NoSuchClientException;

}
