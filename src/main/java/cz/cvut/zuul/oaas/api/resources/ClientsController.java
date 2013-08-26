package cz.cvut.zuul.oaas.api.resources;

import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.models.ImplicitClientDetails;
import cz.cvut.zuul.oaas.services.ClientsService;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
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
public class ClientsController {

    private static final String SELF_URI = "/v1/clients/";

    private @Setter ClientsService clientsService;

    
    //////////  API methods  //////////

    @ResponseBody
    @RequestMapping(value = "{clientId}", method = GET)
    public ClientDTO getClientDetails(@PathVariable String clientId) {
        ClientDTO dto = clientsService.findClientById(clientId);
        return dto;
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    public void createClientDetails(@RequestBody ClientDTO client, HttpServletResponse response) {
        String clientId = clientsService.createClient(client);

        // send redirect to URI of the created client (i.e. api/clients/{clientId}/)
        response.setHeader("Location", SELF_URI + clientId);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = DELETE)
    public void removeClientDetails(@PathVariable String clientId) {
        clientsService.removeClient(clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/secret", method = PUT)
    public void resetClientSecret(@PathVariable String clientId) {
        clientsService.resetClientSecret(clientId);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources", method = PUT)
    public void addResourceToClientDetails(@PathVariable String clientId, @Valid @RequestBody String resourceId) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getResourceIds().add(resourceId)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources/{resourceId}", method = DELETE)
    public void deleteResourceFromClientDetails(@PathVariable String clientId, @PathVariable String resourceId) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getResourceIds().remove(resourceId)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes", method = PUT)
    public void addScopeToClientDetails(@PathVariable String clientId, @RequestBody String scope) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getScope().add(scope)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes/{scope}", method = DELETE)
    public void deleteScopeFromClientDetails(@PathVariable String clientId, @PathVariable String scope) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getScope().remove(scope)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants", method = PUT)
    public void addGrantToClientDetails(@PathVariable String clientId, @RequestBody String grantType) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getAuthorizedGrantTypes().add(grantType)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants/{grantType}", method = DELETE)
    public void deleteGrantFromClientDetails(@PathVariable String clientId, @PathVariable String grantType) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getAuthorizedGrantTypes().remove(grantType)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles", method = PUT)
    public void addRoleToClientDetails(@PathVariable String clientId, @RequestBody String role) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getAuthorities().add(role)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles/{role}", method = DELETE)
    public void deleteRoleFromClientDetails(@PathVariable String clientId, @PathVariable String role) {
        ClientDTO client = clientsService.findClientById(clientId);

        if (client.getAuthorities().remove(role)) {
            clientsService.updateClient(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/redirect-url", method = PUT)
    public void addRedirectUriToClientDetails(@PathVariable String clientId, @RequestBody String redirectUri) {
        ClientDTO client = clientsService.findClientById(clientId);

        client.getRegisteredRedirectUri().clear();
        client.getRegisteredRedirectUri().add(redirectUri);
        clientsService.updateClient(client);
    }
   
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/product-name", method = PUT)
    public void addProductNameToClientDetails(@PathVariable String clientId, @RequestBody String productName) {
        ClientDTO client = clientsService.findClientById(clientId);

        client.setProductName(productName);
        clientsService.updateClient(client);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/implicit-client-details/type", method = PUT)
    public void addImplicitClientDetailsToClientDetails(@PathVariable String clientId, @RequestBody String implicitClientType) throws Exception {
        ClientDTO client = clientsService.findClientById(clientId);

        client.setImplicitClientDetails(new ImplicitClientDetails(implicitClientType));
        clientsService.updateClient(client);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/locked", method = PUT)
    public void addLockedToClientDetails(@PathVariable String clientId, @RequestBody String locked) {
        ClientDTO client = clientsService.findClientById(clientId);

        client.setLocked(Boolean.parseBoolean(locked));
        clientsService.updateClient(client);
    }


    //////////  Exception Handlers  //////////

    @ExceptionHandler(NoSuchClientException.class)
    public ResponseEntity<Void> handleNoSuchClientException() {
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<Void> handleClientAlreadyExistsException() {
        return new ResponseEntity<>(CONFLICT);
    }
}
