package cz.cvut.zuul.oaas.api.rest;

import cz.cvut.zuul.oaas.api.models.TokenDTO;
import cz.cvut.zuul.oaas.api.services.TokensService;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    private @Setter TokensService tokensService;
    
    
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
}
