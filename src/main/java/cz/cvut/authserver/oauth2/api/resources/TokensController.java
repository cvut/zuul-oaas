package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.api.models.TokenDetails;
import cz.cvut.authserver.oauth2.dao.AccessTokenDAO;
import cz.cvut.authserver.oauth2.models.Client;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * API for authorization server tokens' management.
 * 
 * @author Tomáš Maňo <tomasmano@gmail.com>
 */
@Controller
@RequestMapping("/v1/tokens")
public class TokensController {

    private static final String SELF_URI = "/v1/tokens/";
    
    private AccessTokenDAO tokenDao;
    
    private ClientDetailsService clientDetails;
    
    @ResponseBody
    @RequestMapping(value = "{tokenValue}", method = GET)
    public TokenDetails getTokenDetails(@PathVariable String tokenValue) {
        PersistableAccessToken token = tokenDao.findOne(tokenValue);
        
        // first check if token is recognized and if it is not expired
        if (token == null) {
            throw new InvalidTokenException("Token was not recognised");
        }
        if (token.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }

        // now load authentication and add all required details necessary for resource provider to response
        OAuth2Authentication authentication = token.getAuthentication();
        AuthorizationRequest clientAuth = authentication.getAuthorizationRequest();
        Authentication userAuth = authentication.getUserAuthentication();

        ClientDetails client = clientDetails.loadClientByClientId(token.getAuthenticatedClientId());

        TokenDetails tokenDetails = new TokenDetails(
                tokenValue, 
                token.getTokenType(), 
                token.getExpiration().getTime(), 
                checkLocked(client), 
                clientAuth.isDenied(), 
                token.getScope(), 
                client, 
                userAuth);
        return tokenDetails;
    }
   
    /**
     * Check if the client which the given token belongs to is locked.
     *
     * @param response
     */
    private boolean checkLocked(ClientDetails client) {
        // additional check for the possibilty that client is locked
        Object lockedValue = client.getAdditionalInformation().get(Client.fields.LOCKED);
        boolean clientLocked = false;
        // for the compatibility reasons, this property is not required, if not present, accountNonLocked property in UserDetails is set to true by default (as is in defalut implementation)
        if (lockedValue != null) {
            clientLocked = Boolean.parseBoolean(lockedValue.toString());
        }
        return clientLocked;
    }

    //////////  Exceptions Handling  //////////
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    public JsonExceptionMapping handleTokenProblem(InvalidTokenException ex) {
        // TODO Should we really return 409 CONFLICT ? Status message from exception is 401
        return new JsonExceptionMapping(CONFLICT.value(), ex.getOAuth2ErrorCode(), ex.getMessage());
    }
    
    //////////  Getters / Setters  //////////

    public AccessTokenDAO getTokenDao() {
        return tokenDao;
    }

    public void setTokenDao(AccessTokenDAO tokenDao) {
        this.tokenDao = tokenDao;
    }

    public ClientDetailsService getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetailsService clientDetails) {
        this.clientDetails = clientDetails;
    }
    
}
