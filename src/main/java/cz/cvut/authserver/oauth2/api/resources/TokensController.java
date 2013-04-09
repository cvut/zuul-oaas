package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.api.models.TokenDetails;
import cz.cvut.authserver.oauth2.dao.AccessTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import cz.cvut.authserver.oauth2.services.ClientsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


/**
 * API for authorization server tokens' management.
 * 
 * @author Tomáš Maňo <tomasmano@gmail.com>
 */
@Controller
@RequestMapping("/v1/tokens")
public class TokensController {

    private AccessTokenDAO tokenDao;
    private ClientsService clientsService;
    
    @ResponseBody
    @RequestMapping(value = "{tokenValue}", method = GET)
    public TokenDetails getTokenDetails(@PathVariable String tokenValue) {
        PersistableAccessToken token = tokenDao.findOne(tokenValue);

        if (token == null) {
            throw new InvalidTokenException("Token was not recognised");
        }

        OAuth2Authentication authentication = token.getAuthentication();
        AuthorizationRequest clientAuth = authentication.getAuthorizationRequest();
        Authentication userAuth = authentication.getUserAuthentication();

        ClientDTO client = null; 
        try {
            client = clientsService.findClientById(token.getAuthenticatedClientId());
        } catch (NoSuchClientException ex) {
            throw new InvalidTokenException("Client doesn't exist anymore");
        }

        return new TokenDetails(token, clientAuth.isDenied(), client, userAuth);
    }


    //////////  Exceptions Handling  //////////

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    public JsonExceptionMapping handleTokenProblem(InvalidTokenException ex) {
        // TODO Should we really return 409 CONFLICT ? Status message from exception is 401
        return new JsonExceptionMapping(CONFLICT.value(), ex.getOAuth2ErrorCode(), ex.getMessage());
    }

    
    //////////  Accessors  //////////

    public void setTokenDao(AccessTokenDAO tokenDao) {
        this.tokenDao = tokenDao;
    }

    public void setClientsService(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

}
