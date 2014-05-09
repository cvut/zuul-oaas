/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.restapi.controllers;

import cz.cvut.zuul.oaas.api.exceptions.ConflictException;
import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.api.services.ClientsService;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * API for authorization server client's management.
 */
@RestController
@RequestMapping("/v1/clients")
public class ClientsController {

    private static final String SELF_URI = "/v1/clients/";

    private @Setter ClientsService clientsService;


    //////////  API methods  //////////

    @RequestMapping(value = "{clientId}", method = GET)
    ClientDTO getClient(@PathVariable String clientId) {
        return clientsService.findClientById(clientId);
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    void createClient(@RequestBody ClientDTO client, HttpServletResponse response) {
        String clientId = clientsService.createClient(client);

        // send redirect to URI of the created client (i.e. api/clients/{clientId}/)
        response.setHeader("Location", SELF_URI + clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{clientId}", method = PUT)
    void updateClient(@PathVariable String clientId, @RequestBody ClientDTO client) {
        if (! clientId.equals(client.getClientId())) {
            throw new ConflictException("clientId could not be changed");
        }
        clientsService.updateClient(client);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = DELETE)
    void removeClient(@PathVariable String clientId) {
        clientsService.removeClient(clientId);
    }
}
