package cz.cvut.zuul.oaas.controllers;

import cz.cvut.oauth.provider.spring.TokenInfo;
import cz.cvut.zuul.oaas.api.models.ErrorResponse;
import cz.cvut.zuul.oaas.services.TokensService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
public class CheckTokenEndpoint {

    private TokensService tokensService;


    @ResponseBody
    @RequestMapping("/check-token")
    TokenInfo checkToken(@RequestParam("access_token") String value) {
        return tokensService.getTokenInfo(value);
    }


    @ResponseBody
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(InvalidTokenException.class)
    ErrorResponse handleInvalidTokenException(InvalidTokenException ex) {
        return ErrorResponse.from(CONFLICT, ex);
    }


    @Required
    public void setTokensService(TokensService tokensService) {
        this.tokensService = tokensService;
    }
}
