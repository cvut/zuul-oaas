package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import cz.cvut.authserver.oauth2.api.models.ErrorResponse;
import cz.cvut.authserver.oauth2.services.ClientsService;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * API for authorization server client's management.
 * 
 * @author Tomáš Maňo <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
@RequestMapping("/v1/clients")
public class ClientsController{

    private static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);
    private static final String SELF_URI = "/v1/clients/";
    
    private ClientsService clientsService;

    
    //////////  API methods  //////////

    @ResponseBody
    @RequestMapping(value = "{clientId}", method = GET)
    public ClientDTO getClientDetails(@PathVariable String clientId) {
        return clientsService.findClientDetailsById(clientId);
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    public void createClientDetails(@RequestBody ClientDTO client, HttpServletResponse response) {
        String clientId = clientsService.createClientDetails(client);
        LOG.info("New client was created: [{}]", client);

        // send redirect to URI of the created client (i.e. api/clients/{clientId}/)
        response.setHeader("Location", SELF_URI + clientId);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = DELETE)
    public void removeClientDetails(@PathVariable String clientId) {
        clientsService.removeClientDetails(clientId);
        LOG.info("Client with id [{}] was removed", clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/secret", method = PUT)
    public void resetClientSecret(@PathVariable String clientId) {
        clientsService.resetClientSecret(clientId);
        LOG.info("Client secret for client id [{}] was reseted", clientId);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources", method = PUT)
    public void addResourceToClientDetails(@PathVariable String clientId, @Valid @RequestBody String resourceId) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getResourceIds().add(resourceId)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources/{resourceId}", method = DELETE)
    public void deleteResourceFromClientDetails(@PathVariable String clientId, @PathVariable String resourceId) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getResourceIds().remove(resourceId)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes", method = PUT)
    public void addScopeToClientDetails(@PathVariable String clientId, @RequestBody String scope) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getScope().add(scope)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes/{scope}", method = DELETE)
    public void deleteScopeFromClientDetails(@PathVariable String clientId, @PathVariable String scope) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getScope().remove(scope)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants", method = PUT)
    public void addGrantToClientDetails(@PathVariable String clientId, @RequestBody String grantType) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getAuthorizedGrantTypes().add(grantType)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants/{grantType}", method = DELETE)
    public void deleteGrantFromClientDetails(@PathVariable String clientId, @PathVariable String grantType) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getAuthorizedGrantTypes().remove(grantType)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles", method = PUT)
    public void addRoleToClientDetails(@PathVariable String clientId, @RequestBody String role) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getAuthorities().add(new SimpleGrantedAuthority(role))) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles/{role}", method = DELETE)
    public void deleteRoleFromClientDetails(@PathVariable String clientId, @PathVariable String role) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        if (client.getAuthorities().remove(new SimpleGrantedAuthority(role))) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/redirect-url", method = PUT)
    public void setRedirectUriToClientDetails(@PathVariable String clientId, @RequestBody String redirectUri) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        client.getRegisteredRedirectUri().clear();
        client.getRegisteredRedirectUri().add(redirectUri);
        clientsService.updateClientDetails(client);
    }
   
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/product-name", method = PUT)
    public void setProductNameToClientDetails(@PathVariable String clientId, @RequestBody String productName) {
        ClientDTO client = clientsService.findClientDetailsById(clientId);

        client.setProductName(productName);
        clientsService.updateClientDetails(client);
    }


    //////////  Exception Handlers  //////////
    
    @ExceptionHandler(NoSuchClientException.class)
    public ResponseEntity<Void> handleNoSuchClientException(NoSuchClientException ex) {
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<Void> handleClientAlreadyExistsException(ClientAlreadyExistsException ex) {
        return new ResponseEntity<>(CONFLICT);
    }

    @ExceptionHandler(BadClientCredentialsException.class)
    public ResponseEntity<Void> handleBadClientCredentialsException(BadClientCredentialsException ex) {
        return new ResponseEntity<>(UNAUTHORIZED);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodConstraintViolationException.class)
    @SuppressWarnings("deprecation") // will be changed after JSR-349 release
    public @ResponseBody ErrorResponse handleValidationError(MethodConstraintViolationException ex) {
        return ErrorResponse.from(BAD_REQUEST, ex);
    }


    ////////  Getters / Setters  ////////

    public ClientsService getClientsService() {
        return clientsService;
    }

    public void setClientsService(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

}
