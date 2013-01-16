package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.api.validators.constraint.ValidUrl;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class UrlConstraintValidator implements ConstraintValidator<ValidUrl, String>{

    UrlValidator urlValidator = new UrlValidator();
    
    @Override
    public void initialize(ValidUrl constraintAnnotation) {
        // nothing
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isNotEmpty(value) && urlValidator.isValid(value);
    }

}
