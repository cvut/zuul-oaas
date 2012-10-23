package cz.cvut.authserver.oauth2.api.validators;

import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import cz.cvut.authserver.oauth2.api.resources.ClientsController;
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
        // otherwise will cause internal server error: org.postgresql.util.PSQLException: ERROR: value too long for type character varying(256)
        if (request.getOldSecret()!=null && request.getOldSecret().length() > MAX_VALUE_LENGTH) {
            errors.rejectValue("oldSecret", "argument.value.too.long", new Object[]{"old_secret", MAX_VALUE_LENGTH}, "Argument value too long");
        }
        if (request.getNewSecret()!=null && request.getNewSecret().length() > MAX_VALUE_LENGTH) {
            errors.rejectValue("newSecret", "argument.value.too.long", new Object[]{"new_secret", MAX_VALUE_LENGTH}, "Argument value too long");
        }

    }
}
