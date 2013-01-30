package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.utils.AuthorizationGrants;
import cz.cvut.authserver.oauth2.utils.RequestContextHolderUtils;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for Authorization Grant type. Validating if it's not null or empty,
 * attribute's lentgh and that attribute is supported authorization grant type.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class AuthorizationGrantValidator implements Validator {

    public static final Logger LOG = LoggerFactory.getLogger(AuthorizationGrantValidator.class);
    private static final int MAX_VALUE_LENGTH = 256;


    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // first convert target to String
        String authorizedGrantTypes = (String) target;
        
        // Small hack:
        // SKIP this validation if it is unadequate (this step is determined by the current request URI)
        String requestURI = RequestContextHolderUtils.getRequestURI();
        if (!requestURI.endsWith("/grants")) {
            LOG.debug("Skipping AuthorizationGrant Validation because validation is not required according the request URI: [{}]", requestURI);
            return;
        }
        
        // #1 : validate required fields
        if (isEmptyOrNull(authorizedGrantTypes)) {
            errors.reject("argument.required", new Object[]{"authorized_grant_types"}, "Argument 'authorized_grant_types' is required");
            return;
        }
        
        // #2 : validate value too long
        if (isValueTooLong(authorizedGrantTypes)) {
            errors.reject("argument.value.too.long", new Object[]{"authorized_grant_types", MAX_VALUE_LENGTH}, "Argument value too long");
            return;
        }
        
        // #3 : validate if it has valid grant type
        String invalidGrant = retriveInvalidGrantType(authorizedGrantTypes);
        if (invalidGrant != null) {
            errors.reject("invalid.grant.type", new Object[]{invalidGrant}, String.format("Grant type %s is invalid", invalidGrant));
            return;
        }
        
    }
    
    private boolean isEmptyOrNull(String arg){
        return !StringUtils.isNotEmpty(arg);
    }
    
    private boolean isEmptyOrNull(Set<String> args) {
        if (args == null) {
            return true;
        }
        if (args.isEmpty()) {
            return true;
        }
        for (String string : args) {
            if (isEmptyOrNull(string)) {
                return true;
            }
        }
        return false;
    }
    
    private String retriveInvalidGrantType(String arg) {
        if (arg.equals(AuthorizationGrants.authCode.get())) {
            return null;
        }
        if (arg.equals(AuthorizationGrants.implict.get())) {
            return null;
        }
        if (arg.equals(AuthorizationGrants.clientCredentials.get())) {
            return null;
        }
        if (arg.equals(AuthorizationGrants.clientCredentials.get())) {
            return null;
        }
        return arg;
    }
    
    private String retriveInvalidGrantType(Set<String> args) {
        for (String string : args) {
            if (retriveInvalidGrantType(string)!=null) {
                return string;
            }
        }
        return null;
    }

    private boolean isValueTooLong(String arg) {
        return arg.length() > MAX_VALUE_LENGTH;
    }

}
