package cz.cvut.authserver.oauth2.api.validators.constraint;

import cz.cvut.authserver.oauth2.models.resource.enums.ResourceVisibility;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom constraint validator implementation for JSR-303 validation constraint
 * 'ValidUrl'.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class VisibilityConstraintValidator implements ConstraintValidator<ValidVisibility, String>{
    
    private Set<String> validValues;

    @Override
    public void initialize(ValidVisibility constraintAnnotation) {
        Set<ResourceVisibility> enums = EnumSet.allOf(ResourceVisibility.class);
        validValues = new HashSet<String>();
        for (ResourceVisibility resourceVisibility : enums) {
            validValues.add(resourceVisibility.get());
        }
        
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validValues.contains(value);
    }

    //////////  Getters / Setters  //////////
    
    public Set<String> getValidValues() {
        return validValues;
    }

    public void setValidValues(Set<String> validValues) {
        this.validValues = validValues;
    }
    
}
