package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.utils.AuthorizationGrants;
import cz.cvut.authserver.oauth2.utils.RequestContextHolerUtils;
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
public class ScopesValidator implements Validator {

    public static final Logger LOG = LoggerFactory.getLogger(ScopesValidator.class);
    private static final int MAX_VALUE_LENGTH = 256;


    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // first convert target to String
        String scopes = (String) target;
        
        // Small hack:
        // SKIP this validation if it is unadequate (this step is determined by the current request URI)
        String requestURI = RequestContextHolerUtils.getRequestURI();
        if (!requestURI.endsWith("/scopes")) {
            LOG.debug("Skipping Scope Validation because validation is not required according the request URI: [{}]", requestURI);
            return;
        }
        
        // #1 : validate required fields
        if (isEmptyOrNull(scopes)) {
            errors.reject("argument.required", new Object[]{"scopes"}, "Argument 'scopes' is required");
            return;
        }
        
        // #2 : validate value too long
        if (isValueTooLong(scopes)) {
            errors.reject("argument.value.too.long", new Object[]{"scopes", MAX_VALUE_LENGTH}, "Argument value too long");
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

    private boolean isValueTooLong(String arg) {
        return arg.length() > MAX_VALUE_LENGTH;
    }

}
