package cz.cvut.authserver.oauth2.api.validators.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import cz.cvut.authserver.oauth2.api.validators.UrlConstraintValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = UrlConstraintValidator.class)
@Documented
public @interface ValidUrl {

    String message() default "{validator.invalid.url}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}