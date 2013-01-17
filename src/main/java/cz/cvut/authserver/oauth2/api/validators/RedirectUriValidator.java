package cz.cvut.authserver.oauth2.api.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator for redirect uri. Validating if it's not null or empty,
 * attribute's lentgh and correct url syntax.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class RedirectUriValidator {
    
    public static final Logger LOG = LoggerFactory.getLogger(RedirectUriValidator.class);
    private static final int MAX_VALUE_LENGTH = 256;
    
}
