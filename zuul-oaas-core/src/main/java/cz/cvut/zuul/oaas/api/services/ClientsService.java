package cz.cvut.zuul.oaas.api.services;

import cz.cvut.zuul.oaas.api.exceptions.NoSuchClientException;
import cz.cvut.zuul.oaas.api.models.ClientDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Validated
public interface ClientsService {
    
    ClientDTO findClientById(String clientId) throws NoSuchClientException;

    String createClient(@Valid ClientDTO client);

    void updateClient(@Valid ClientDTO client) throws NoSuchClientException;
    
    void removeClient(String clientId) throws NoSuchClientException;
    
    void resetClientSecret(String clientId) throws NoSuchClientException;

}
