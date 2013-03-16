package cz.cvut.authserver.oauth2.controllers;

import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.oauth.provider.spring.TokenInfo;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * Controller which decodes access tokens for clients who are not able to do so
 * (or where opaque token values are used).
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
public class CheckTokenEndpoint {

    private ResourceServerTokenServices resourceServerTokenServices;


    @RequestMapping(value = "/check-token")
    public @ResponseBody TokenInfo checkToken(@RequestParam("access_token") String value) {
        OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(value);

        // first check if token is recognized and if it is not expired
        if (token == null) {
            throw new InvalidTokenException("Token was not recognised");
        }
        if (token.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }

        OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(value);
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
    public void setTokenServices(ResourceServerTokenServices resourceServerTokenServices) {
        this.resourceServerTokenServices = resourceServerTokenServices;
    }

}
