package cz.cvut.zuul.oaas.restapi.controllers;

import cz.cvut.zuul.oaas.api.models.TokenInfo;
import cz.cvut.zuul.oaas.api.services.TokensService;
import lombok.Setter;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
public class CheckTokenEndpoint {

    private @Setter TokensService tokensService;


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
}
