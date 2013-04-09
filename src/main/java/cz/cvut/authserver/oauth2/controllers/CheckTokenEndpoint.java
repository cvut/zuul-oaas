package cz.cvut.authserver.oauth2.controllers;

import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.dao.AccessTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import cz.cvut.authserver.oauth2.services.ClientsService;
import cz.cvut.oauth.provider.spring.TokenInfo;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import org.springframework.security.oauth2.provider.NoSuchClientException;

/**
 * Controller which decodes access tokens for clients who are not able to do so
 * (or where opaque token values are used).
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
public class CheckTokenEndpoint {

    //TODO there should be a service
    private AccessTokenDAO dao;
    private ClientsService clientsService;


    @RequestMapping(value = "/check-token")
    public @ResponseBody TokenInfo checkToken(@RequestParam("access_token") String value) {
        PersistableAccessToken token = dao.findOne(value);

        // first check if token is recognized and if it is not expired
        if (token == null) {
            throw new InvalidTokenException("Token was not recognised");
        }
        if (token.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }
        ClientDTO client = null;
        try {
            client = clientsService.findClientById(token.getAuthenticatedClientId());
        } catch (NoSuchClientException ex) {
            throw new InvalidTokenException("Client doesn't exist anymore");
        }
        if (client.isLocked()) {
            throw new InvalidTokenException("The client is locked");
        }

        OAuth2Authentication authentication = token.getAuthentication();
        AuthorizationRequest clientAuth = authentication.getAuthorizationRequest();

        TokenInfo info = new TokenInfo();

        info.setAudience(clientAuth.getResourceIds());
        info.setClientId(clientAuth.getClientId());
        info.setClientAuthorities(clientAuth.getAuthorities());
        info.setExpiresIn(token.getExpiresIn());
        info.setScope(token.getScope());

        if (!authentication.isClientOnly()) {
            Authentication userAuth = authentication.getUserAuthentication();
            info.setUserAuthorities(userAuth.getAuthorities());
            info.setUserId(userAuth.getName());
        }

        return info;
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

    @Required
    public void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.dao = accessTokenDAO;
    }

    public void setClientsService(ClientsService clientsService) {
        this.clientsService = clientsService;
    }
    
}
