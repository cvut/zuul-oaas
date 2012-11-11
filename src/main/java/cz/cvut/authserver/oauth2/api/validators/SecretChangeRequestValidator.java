package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class SecretChangeRequestValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(SecretChangeRequestValidator.class);
    private static final int MAX_VALUE_LENGTH = 256;

    @Override
    public boolean supports(Class<?> clazz) {
        return SecretChangeRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "oldSecret", "argument.required", new Object[]{"old_secret"});
        ValidationUtils.rejectIfEmpty(errors, "newSecret", "argument.required", new Object[]{"new_secret"});
        SecretChangeRequest request = (SecretChangeRequest) target;
        String oldSecret = request.getOldSecret();
        String newSecret = request.getNewSecret();
        
        // otherwise will cause the internal server error: org.postgresql.util.PSQLException: ERROR: value too long for type character varying(256)
        if (argumentValueTooLongCondition(oldSecret)) {
            errors.rejectValue("oldSecret", "argument.value.too.long", new Object[]{"old_secret", MAX_VALUE_LENGTH}, "Argument value too long");
        }
        if (argumentValueTooLongCondition(newSecret)) {
            errors.rejectValue("newSecret", "argument.value.too.long", new Object[]{"new_secret", MAX_VALUE_LENGTH}, "Argument value too long");
        }
        
        // validate that attributes contains only the ascii printable characters
        if (argumentContainsInvalidCharacters(oldSecret)) {
            errors.rejectValue("oldSecret", "argument.contains.invalid.characters", new Object[]{"old_secret"}, "Attribute contains invalid characters");
        }
        if (argumentContainsInvalidCharacters(newSecret)) {
            errors.rejectValue("newSecret", "argument.contains.invalid.characters", new Object[]{"new_secret"}, "Attribute contains invalid characters");
        }
    }

    private boolean argumentValueTooLongCondition(String arg) {
        return arg != null && arg.length() > MAX_VALUE_LENGTH;
    }

    /**
     * Condition required by {@link http://tools.ietf.org/html/draft-ietf-oauth-v2-31#appendix-A.2}. Argument
     * client_secret allows only ascii printable characters.
     */
    private boolean argumentContainsInvalidCharacters(String arg) {
        return !StringUtils.isAsciiPrintable(arg);
    }
}
