package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Validated
public interface ClientsService {
    
    ClientDTO findClientDetailsById(String clientId) throws OAuth2Exception;

    String createClientDetails(@Valid ClientDTO client) throws ClientAlreadyExistsException;

    void updateClientDetails(@Valid ClientDTO client) throws NoSuchClientException;
    
    void removeClientDetails(String clientId) throws NoSuchClientException;
    
    void resetClientSecret(String clientId) throws NoSuchClientException;

}
