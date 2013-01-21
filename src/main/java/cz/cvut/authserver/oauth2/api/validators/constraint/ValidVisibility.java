package cz.cvut.authserver.oauth2.api.validators.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Custom JSR-303 validation constraint for valid visibility values.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VisibilityConstraintValidator.class)
@Documented
public @interface ValidVisibility {
    
    String message() default "{validator.invalid.visibilty.value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    
}
