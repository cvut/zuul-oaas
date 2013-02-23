package cz.cvut.authserver.oauth2.services;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface ClientsService {
    
    ClientDetails findClientDetailsById(@PathVariable String clientId) throws OAuth2Exception;

    void createClientDetails(BaseClientDetails client) throws ClientAlreadyExistsException;

    void updateClientDetails(ClientDetails client) throws NoSuchClientException;
    
    void removeClientDetails(String clientId) throws NoSuchClientException;
    
    void resetClientSecret(String clientId) throws NoSuchClientException;

}
