package cz.cvut.zuul.oaas.restapi.controllers;

import cz.cvut.zuul.oaas.api.exceptions.ConflictException;
import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.api.services.ClientsService;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * API for authorization server client's management.
 */
@Controller
@RequestMapping("/v1/clients")
public class ClientsController {

    private static final String SELF_URI = "/v1/clients/";

    private @Setter ClientsService clientsService;

    
    //////////  API methods  //////////

    @ResponseBody
    @RequestMapping(value = "{clientId}", method = GET)
    public ClientDTO getClient(@PathVariable String clientId) {
        return clientsService.findClientById(clientId);
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    public void createClient(@RequestBody ClientDTO client, HttpServletResponse response) {
        String clientId = clientsService.createClient(client);

        // send redirect to URI of the created client (i.e. api/clients/{clientId}/)
        response.setHeader("Location", SELF_URI + clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{clientId}", method = PUT)
    public void updateClient(@PathVariable String clientId, @RequestBody ClientDTO client) {
        if (! clientId.equals(client.getClientId())) {
            throw new ConflictException("clientId could not be changed");
        }
        clientsService.updateClient(client);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = DELETE)
    public void removeClient(@PathVariable String clientId) {
        clientsService.removeClient(clientId);
    }
}
