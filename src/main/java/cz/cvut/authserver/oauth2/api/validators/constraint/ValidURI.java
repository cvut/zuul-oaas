package cz.cvut.authserver.oauth2.api.validators.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * JSR-303 constraint for URI validation.
 * 
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, ANNOTATION_TYPE})
@Constraint(validatedBy = ValidURIConstraintValidator.class)
public @interface ValidURI {

    String message() default "{validator.ValidURI.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * May URI be relative?
     */
    boolean relative() default true;

    /**
     * May URI contain query string?
     */
    boolean query() default true;

    /**
     * May URI contain fragment?
     */
    boolean fragment() default true;
}
