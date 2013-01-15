package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import cz.cvut.authserver.oauth2.api.validators.ClientDetailsValidator;
import cz.cvut.authserver.oauth2.generators.OAuth2ClientCredentialsGenerator;
import java.util.LinkedHashSet;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
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
public class ClientsController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);
    
    private String apiVersion;
    
    private ClientDetailsService clientDetailsService;
    
    private ClientRegistrationService clientRegistrationService;
    
    private ClientDetailsValidator clientDetailsValidator;
    
    private OAuth2ClientCredentialsGenerator oauth2ClientCredentialsGenerator;
    
    private MessageSource messageSource;

    
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(clientDetailsValidator);
    }
    
    //////////  API methods  //////////

    @ResponseBody
    @RequestMapping(value = "{clientId}", method = GET)
    public ClientDetails getClientDetails(@PathVariable String clientId) throws OAuth2Exception {
        return clientDetailsService.loadClientByClientId(clientId);
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    public void createClientDetails(@Valid @RequestBody BaseClientDetails client, HttpServletResponse response) throws Exception {

        // generate oauth2 client credentials
        String clientId = oauth2ClientCredentialsGenerator.generateClientId();
        String clientSecret = oauth2ClientCredentialsGenerator.generateClientSecret();
        
        // setting necessary fields
        client.setClientId(clientId);
        client.setClientSecret(clientSecret);
        client.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_CLIENT"));
        
        // save
        clientRegistrationService.addClientDetails(client);

        // send redirect to URI of the created client (i.e. api/clients/{clientId}/)
        response.setHeader("Location", String.format("/%s/clients/%s", apiVersion,
                client.getClientId()));
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = DELETE)
    public void removeClientDetails(@PathVariable String clientId) throws NoSuchClientException {
        clientRegistrationService.removeClientDetails(clientId);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}", method = PUT)
    public void resetClientSecret(@PathVariable String clientId) throws NoSuchClientException {
        String newSecret = oauth2ClientCredentialsGenerator.generateClientSecret();
        clientRegistrationService.updateClientSecret(clientId, newSecret);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes", method = PUT)
    public void addScopeToClientDetails(@PathVariable String clientId, @RequestBody String scope) throws Exception{
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        BaseClientDetails baseClientDetails = (BaseClientDetails) clientDetails;
        baseClientDetails.setScope(new LinkedHashSet<String>());
        baseClientDetails.getScope().add(scope);
        clientDetails = baseClientDetails;
        clientRegistrationService.updateClientDetails(clientDetails);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/scopes", method = DELETE)
    public void removeScopeFromClientDetails(@PathVariable String clientId, String scope) throws Exception{
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        if (clientDetails.getScope().contains(scope)) {
            clientDetails.getScope().remove(scope);
            clientRegistrationService.updateClientDetails(clientDetails);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants", method = PUT)
    public void addGrantToClientDetails(@PathVariable String clientId, String grantType) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        clientDetails.getScope().add(grantType);
        clientRegistrationService.updateClientDetails(clientDetails);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/grants", method = DELETE)
    public void deleteGrantFromClientDetails(@PathVariable String clientId, String grantType) throws Exception{
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        if (clientDetails.getAuthorizedGrantTypes().contains(grantType)) {
            clientDetails.getAuthorizedGrantTypes().remove(grantType);
            clientRegistrationService.updateClientDetails(clientDetails);
        }
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles", method = PUT)
    public void addRoleToClientDetails(@PathVariable String clientId, String role) throws Exception {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        clientDetails.getAuthorities().addAll(AuthorityUtils.createAuthorityList(role));
        clientRegistrationService.updateClientDetails(clientDetails);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/roles", method = DELETE)
    public void deleteRoleFromClientDetails(@PathVariable String clientId, String role) throws Exception{
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //BaseClientDetails collections are always initialized when new BaseClientDetails instance is created
        GrantedAuthority removed = new SimpleGrantedAuthority(role);
        if (clientDetails.getAuthorities().contains(removed)) {
            clientDetails.getAuthorities().remove(removed);
            clientRegistrationService.updateClientDetails(clientDetails);
        }
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

    @Deprecated
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/secret", method = PUT)
    public void updateClientSecret(@PathVariable String clientId, @Valid @RequestBody SecretChangeRequest changeRequest) throws Exception {

        ClientDetails clientDetails;
        try {
            clientDetails = clientDetailsService.loadClientByClientId(clientId);
        } catch (ClientAuthenticationException e) {
            LOG.warn("Attempt to change client secret for client {0}.", clientId);
            throw new NoSuchClientException("No such client: " + clientId);
        }

        if (!clientDetails.getClientSecret().equals(changeRequest.getOldSecret())) {
            LOG.warn("Client secret change not allowed for {0}. Invalid old client secret provided." + clientId);
            throw new BadClientCredentialsException();
        }

        // TODO it MUST check given old password against what we load and if the client is authorized to do that
        // see org.cloudfoundry.identity.uaa.oauth.ClientAdminEndpoints for inspiration

        clientRegistrationService.updateClientSecret(clientId, changeRequest.getNewSecret());
    }
    
    /**
     * Updates resources that given client can access with given scopes.
     * 
     * @param clientId client id of the client
     * @param request request containing resources that client wants access with given scope
     */
    @Deprecated
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{clientId}/resources", method = PUT)
    public void updateAccessingResurces(@PathVariable String clientId, @RequestBody Object request){
        
        // request shall conaint data enabling update of:
        //          - 'resourceIds' property in BaseClientDetails
        //          - 'scope' property in BaseClientDetails
        
    }

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
    JsonExceptionMapping handleMethodArgumentNotValidException(MethodArgumentNotValidException error) {
        BindingResult bindingResult = error.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        String errorMessage = constructErrorMessage(errors);
        return new JsonExceptionMapping(bindingResult, HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    private String constructErrorMessage(List<ObjectError> errors) {
        String errorMessage = "";
        for (ObjectError objectError : errors) {
            errorMessage = errorMessage.concat(messageSource.getMessage(objectError.getCode(), objectError.getArguments(), null));
        }
        return errorMessage;
    }

    ////////  Getters / Setters  ////////
    
    
    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    public void setClientRegistrationService(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    public ClientDetailsValidator getClientDetailsValidator() {
        return clientDetailsValidator;
    }

    public void setClientDetailsValidator(ClientDetailsValidator clientDetailsValidator) {
        this.clientDetailsValidator = clientDetailsValidator;
    }

    public OAuth2ClientCredentialsGenerator getOauth2ClientCredentialsGenerator() {
        return oauth2ClientCredentialsGenerator;
    }

    public void setOauth2ClientCredentialsGenerator(OAuth2ClientCredentialsGenerator oauth2ClientCredentialsGenerator) {
        this.oauth2ClientCredentialsGenerator = oauth2ClientCredentialsGenerator;
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
