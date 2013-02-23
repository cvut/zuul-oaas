package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.models.AuthorizationGrants;
import cz.cvut.authserver.oauth2.utils.RequestContextHolderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for Authorization Grant type. Validating if it's not null or empty,
 * attribute's lentgh and that attribute is supported authorization grant type.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class AuthorizationGrantValidator implements Validator {

    public static final Logger LOG = LoggerFactory.getLogger(AuthorizationGrantValidator.class);


    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // first convert target to String
        String grantType = (String) target;
        
        // Small hack:
        // SKIP this validation if it is unadequate (this step is determined by the current request URI)
        String requestURI = RequestContextHolderUtils.getRequestURI();
        if (!requestURI.endsWith("/grants")) {
            LOG.debug("Skipping AuthorizationGrant Validation because validation is not required according the request URI: [{}]", requestURI);
            return;
        }

        if (! AuthorizationGrants.contains(grantType)) {
            errors.reject("invalid.grant.type", new Object[]{grantType}, String.format("Grant type %s is invalid", grantType));
            return;
        }
        
    }

}
