package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 *
 * @author Tomáš Maňo <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
@RequestMapping(value="/v1/clients")
public class ClientsController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);

    private ClientDetailsService clientDetailsService;
    private ClientRegistrationService clientRegistrationService;



    @ResponseBody
    @RequestMapping(value="{clientId}", method=GET)
    public ClientDetails getClientDetails(@PathVariable String clientId) throws Exception {
        return clientDetailsService.loadClientByClientId(clientId);
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method=POST)
    public void createClientDetails(@RequestBody BaseClientDetails client) throws Exception {
        // TODO it MUST valide given data before insert!

        clientRegistrationService.addClientDetails(client);

        // TODO should send redirect to URI of the created client (i.e. api/clients/{clientId}/)
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value="{clientId}", method=PUT)
    public void updateClientDetails(@RequestBody BaseClientDetails client,
                                    @PathVariable String clientId) throws Exception {

        Assert.state(clientId.equals(client.getClientId()), String.format(
                "The client_id %s does not match the URL %s", client.getClientId(), clientId));

        ClientDetails details = client;
        try {
            ClientDetails existing = getClientDetails(clientId);
            // TODO it should sync given client with existing one
        } catch (Exception ex) {
            LOG.warn("Couldn't fetch client details for client_id: " + clientId, ex);
        }
        // TODO it MUST valide given data before update!
        clientRegistrationService.updateClientDetails(details);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value="{clientId}", method=DELETE)
    public void removeClientDetails(@PathVariable String clientId) throws Exception {
        // TODO check?
        clientRegistrationService.removeClientDetails(clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value="{clientId}/secret", method=PUT)
    public void updateClientSecret(@PathVariable String clientId, @RequestBody SecretChangeRequest changeRequest) throws Exception {

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        // TODO it MUST check given old password against what we load and if the client is authorized to do that
        // see org.cloudfoundry.identity.uaa.oauth.ClientAdminEndpoints for inspiration

        clientRegistrationService.updateClientSecret(clientId, changeRequest.getNewSecret());
    }


    @ExceptionHandler(NoSuchClientException.class)
    public ResponseEntity<Void> handleNoSuchClient(NoSuchClientException ex) {
            return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<Void> handleClientAlreadyExists(ClientAlreadyExistsException ex) {
            return new ResponseEntity<>(CONFLICT);
    }


    ////////  Getters / Setters  ////////

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    public void setClientRegistrationService(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }
    
}

