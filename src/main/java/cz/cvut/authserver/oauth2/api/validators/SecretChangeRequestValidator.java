package cz.cvut.authserver.oauth2.api.validators;

import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class SecretChangeRequestValidator implements Validator{

    @Override
    public boolean supports(Class<?> clazz) {
        return SecretChangeRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "oldSecret", "argument.required", new Object[]{"oldSecret"});
        ValidationUtils.rejectIfEmpty(errors, "newSecret", "argument.required", new Object[]{"newSecret"});
    }
    
}
