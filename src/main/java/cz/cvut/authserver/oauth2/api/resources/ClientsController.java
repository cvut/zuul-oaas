package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.api.validators.ClientsResourcesCompositeValidator;
import cz.cvut.authserver.oauth2.services.ClientsService;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * API for authorization server client's management.
 * 
 * @author Tomáš Maňo <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
@RequestMapping(value = "/v1/clients")
public class ClientsController{

    private static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);
    private static final String SELF_URI = "/v1/clients/";
    
    private ClientsService clientsService;
    
    private ClientsResourcesCompositeValidator clientsResourcesCompositeValidator;
    
    private MessageSource messageSource;

    
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(clientsResourcesCompositeValidator);
    }
    
    //////////  API methods  //////////

    @ResponseBody
    @RequestMapping(value = "{clientId}", method = GET)
    public ClientDetails getClientDetails(@PathVariable String clientId) throws NoSuchClientException {
        return clientsService.findClientDetailsById(clientId);
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    public void createClientDetails(@Valid @RequestBody BaseClientDetails client, HttpServletResponse response) throws ClientAlreadyExistsException {

        clientsService.createClientDetails(client);
        LOG.info("New client was created: [{}]", client);

        // send redirect to URI of the created client (i.e. api/clients/{clientId}/)
        response.setHeader("Location", SELF_URI + client.getClientId());
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = DELETE)
    public void removeClientDetails(@PathVariable String clientId) throws NoSuchClientException {
        clientsService.removeClientDetails(clientId);
        LOG.info("Client with id [{}] was removed", clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/secret", method = PUT)
    public void resetClientSecret(@PathVariable String clientId) throws NoSuchClientException {
        clientsService.resetClientSecret(clientId);
        LOG.info("Client secret for client id [{}] was reseted", clientId);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources", method = PUT)
    public void addResourceToClientDetails(@PathVariable String clientId, @Valid @RequestBody String resourceId) {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getResourceIds().add(resourceId)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources/{resourceId}", method = DELETE)
    public void deleteResourceFromClientDetails(@PathVariable String clientId, @PathVariable String resourceId) {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getResourceIds().remove(resourceId)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes", method = PUT)
    public void addScopeToClientDetails(@PathVariable String clientId, @Valid @RequestBody String scope) {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getScope().add(scope)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes/{scope}", method = DELETE)
    public void deleteScopeFromClientDetails(@PathVariable String clientId, @PathVariable String scope) throws URIException {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getScope().remove(URIUtil.decode(scope))) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants", method = PUT)
    public void addGrantToClientDetails(@PathVariable String clientId, @Valid @RequestBody String grantType) {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getAuthorizedGrantTypes().add(grantType)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants/{grantType}", method = DELETE)
    public void deleteGrantFromClientDetails(@PathVariable String clientId, @Valid @PathVariable String grantType) {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getAuthorizedGrantTypes().remove(grantType)) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles", method = PUT)
    public void addRoleToClientDetails(@PathVariable String clientId, @RequestBody String role) {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getAuthorities().add(new SimpleGrantedAuthority(role))) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles/{role}", method = DELETE)
    public void deleteRoleFromClientDetails(@PathVariable String clientId, @PathVariable String role) {
        ClientDetails client = getMutableClientDetails(clientId);

        if (client.getAuthorities().remove(new SimpleGrantedAuthority(role))) {
            clientsService.updateClientDetails(client);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/redirect-url", method = PUT)
    public void setRedirectUriToClientDetails(@PathVariable String clientId, @RequestBody String redirectUri) {
        ClientDetails client = getMutableClientDetails(clientId);

        client.getRegisteredRedirectUri().clear();
        client.getRegisteredRedirectUri().add(redirectUri);
        clientsService.updateClientDetails(client);
    }
   
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/product-name", method = PUT)
    public void setBrandingInformationToClientDetails(@PathVariable String clientId, @RequestBody String brand) throws OAuth2Exception, NoSuchClientException {
        BaseClientDetails client = getMutableClientDetails(clientId);

        client.addAdditionalInformation("product-name", brand);
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody JsonExceptionMapping handleMethodArgumentNotValidException(MethodArgumentNotValidException error) throws Exception {
        BindingResult bindingResult = error.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        String errorMessage = constructErrorMessage(errors);
        
        return new JsonExceptionMapping(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }


    //////////  Helpers //////////

    private BaseClientDetails getMutableClientDetails(String clientId) {
        ClientDetails original = clientsService.findClientDetailsById(clientId);
        BaseClientDetails copy = new BaseClientDetails(original);

        copy.setScope(original.getScope());
        copy.setResourceIds(original.getResourceIds());
        copy.setAuthorizedGrantTypes(original.getAuthorizedGrantTypes());
        copy.setRegisteredRedirectUri(original.getRegisteredRedirectUri());
        copy.setAuthorities(original.getAuthorities());

        return copy;
    }

    private String constructErrorMessage(List<ObjectError> errors) throws Exception {
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError objectError : errors) {
            try {
                errorMessage = errorMessage.append(messageSource.getMessage(objectError.getCode(), objectError.getArguments(), null));
            } catch (Exception e) {
                LOG.error("Error during parsing properties file with validation errors: {}", e.getMessage());
                throw new Exception(e);
            }
        }
        return errorMessage.toString();
    }


    ////////  Getters / Setters  ////////

    public ClientsService getClientsService() {
        return clientsService;
    }

    public void setClientsService(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    public ClientsResourcesCompositeValidator getClientsResourcesCompositeValidator() {
        return clientsResourcesCompositeValidator;
    }

    public void setClientsResourcesCompositeValidator(ClientsResourcesCompositeValidator clientsResourcesCompositeValidator) {
        this.clientsResourcesCompositeValidator = clientsResourcesCompositeValidator;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
