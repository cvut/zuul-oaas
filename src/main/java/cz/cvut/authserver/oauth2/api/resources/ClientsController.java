package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.api.validators.AuthorizationGrantValidator;
import cz.cvut.authserver.oauth2.api.validators.ClientDetailsValidator;
import cz.cvut.authserver.oauth2.api.validators.ClientsResourcesCompositeValidator;
import cz.cvut.authserver.oauth2.services.ClientsService;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * API for autorization server client's management.
 * 
 * @author Tomáš Maňo <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
@RequestMapping(value = "/v1/clients")
public class ClientsController{

    private static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);
    
    private String apiVersion;
    
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
    public void createClientDetails(@Valid @RequestBody BaseClientDetails client, HttpServletResponse response) throws Exception {

        clientsService.createClientDetails(client);

        // send redirect to URI of the created client (i.e. api/clients/{clientId}/)
        response.setHeader("Location", String.format("/%s/clients/%s", apiVersion,
                client.getClientId()));
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = DELETE)
    public void removeClientDetails(@PathVariable String clientId) throws NoSuchClientException {
        clientsService.removeClientDetails(clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = PUT)
    public void resetClientSecret(@PathVariable String clientId) throws NoSuchClientException {
        clientsService.resetClientSecret(clientId);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources", method = PUT)
    public void addResourceToClientDetails(@PathVariable String clientId, @RequestBody String resourceId) throws Exception {
        clientsService.addResourceToClientDetails(clientId, resourceId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources", method = DELETE)
    public void removeResourceFromClientDetails(@PathVariable String clientId, @RequestBody String resourceId) throws Exception {
        clientsService.addResourceToClientDetails(clientId, resourceId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes", method = PUT)
    public void addScopeToClientDetails(@PathVariable String clientId, @Valid @RequestBody String scope) throws Exception {
        clientsService.addScopeToClientDetails(clientId, scope);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes", method = DELETE)
    public void removeScopeFromClientDetails(@PathVariable String clientId, @RequestBody String scope) throws Exception {
        clientsService.removeScopeFromClientDetails(clientId, scope);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants", method = PUT)
    public void addGrantToClientDetails(@PathVariable String clientId, @Valid @RequestBody String grantType) throws Exception {
        clientsService.addGrantToClientDetails(clientId, grantType);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants", method = DELETE)
    public void deleteGrantFromClientDetails(@PathVariable String clientId, @Valid @RequestBody String grantType) throws Exception {
        clientsService.deleteGrantFromClientDetails(clientId, grantType);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles", method = PUT)
    public void addRoleToClientDetails(@PathVariable String clientId, @RequestBody String role) throws Exception {
        clientsService.addRoleToClientDetails(clientId, role);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles", method = DELETE)
    public void deleteRoleFromClientDetails(@PathVariable String clientId, @RequestBody String role) throws Exception {
        clientsService.deleteRoleFromClientDetails(clientId, role);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/redirect-url", method = PUT)
    public void addRedirectUriToClientDetails(@PathVariable String clientId, @RequestBody String redirectUri) throws Exception {
        clientsService.addRedirectUriToClientDetails(clientId, redirectUri);
    }

    @Deprecated
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/redirect-url", method = DELETE)
    public void deleteRedirectUriFromClientDetails(@PathVariable String clientId, @RequestBody String redirectUri) throws Exception {
        clientsService.deleteRoleFromClientDetails(clientId, redirectUri);
    }
   
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/product-name", method = PUT)
    public void addBrandingInformationToClientDetails(@PathVariable String clientId, @RequestBody String brand) throws Exception {
        clientsService.addBrandingInformationToClientDetails(clientId, brand);
    }

    //////////  Depracated Methods  //////////
   
//    @ResponseStatus(NO_CONTENT)
//    @RequestMapping(value = "{clientId}", method = PUT)
//    @Deprecated
//    public void updateClientDetails(@RequestBody BaseClientDetails client,
//            @PathVariable String clientId) throws NoSuchClientException {
//
//        Assert.state(clientId.equals(client.getClientId()), String.format(
//                "The client_id %s does not match the URL %s", client.getClientId(), clientId));
//
//        ClientDetails details = client;
//        try {
//            ClientDetails existing = getClientDetails(clientId);
//            // TODO it should sync given client with existing one
//        } catch (Exception ex) {
//            LOG.warn("Couldn't fetch client details for client_id: " + clientId, ex);
//        }
//        // TODO it MUST valide given data before update!
//        clientRegistrationService.updateClientDetails(details);
//    }


    //////////  Exception Handlers  //////////
    
    @ExceptionHandler(NoSuchClientException.class)
    public ResponseEntity<Void> handleNoSuchClient(NoSuchClientException ex) {
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<Void> handleClientAlreadyExists(ClientAlreadyExistsException ex) {
        return new ResponseEntity<>(CONFLICT);
    }

    @ExceptionHandler(BadClientCredentialsException.class)
    public ResponseEntity<Void> handleBadClientCredentialsException(BadClientCredentialsException ex) {
        return new ResponseEntity<>(UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    JsonExceptionMapping handleMethodArgumentNotValidException(MethodArgumentNotValidException error) throws Exception {
        BindingResult bindingResult = error.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        String errorMessage = constructErrorMessage(errors);
        
        return new JsonExceptionMapping(bindingResult, HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    private String constructErrorMessage(List<ObjectError> errors) throws Exception {
        String errorMessage = "";
        for (ObjectError objectError : errors) {
            try {
                errorMessage = errorMessage.concat(messageSource.getMessage(objectError.getCode(), objectError.getArguments(), null));
            } catch (Exception e) {
                LOG.error("Error during parsing properties file with validation errors: {}", e.getMessage());
                throw new Exception(e);
            }
        }
        return errorMessage;
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

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

}
