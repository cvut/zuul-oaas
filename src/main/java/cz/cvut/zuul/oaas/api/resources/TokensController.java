package cz.cvut.zuul.oaas.api.resources;

import cz.cvut.zuul.oaas.api.models.ErrorResponse;
import cz.cvut.zuul.oaas.api.models.TokenDTO;
import cz.cvut.zuul.oaas.api.resources.exceptions.NoSuchTokenException;
import cz.cvut.zuul.oaas.services.TokensService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


/**
 * API for authorization server tokens' management.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
@RequestMapping("/v1/tokens")
public class TokensController {

    private TokensService tokensService;
    
    @ResponseBody
    @RequestMapping(value = "{tokenValue}", method = GET)
    TokenDTO getTokenDetails(@PathVariable String tokenValue) {
        return tokensService.getToken(tokenValue);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "{tokenValue}", method = DELETE)
    public void invalidateToken(@PathVariable String tokenValue){
        tokensService.invalidateToken(tokenValue);
    }


    //////////  Exceptions Handling  //////////

    @ResponseBody
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NoSuchTokenException.class)
    public ErrorResponse handleNoSuchTokenException(NoSuchTokenException ex) {
        return ErrorResponse.from(NOT_FOUND, ex);
    }

    
    //////////  Accessors  //////////

    @Required
    public void setTokensService(TokensService tokensService) {
        this.tokensService = tokensService;
    }
}
