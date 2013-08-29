package cz.cvut.zuul.oaas.api.validators;

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
     * May URI be relative? [default true]
     */
    boolean relative() default true;

    /**
     * May URI contain query string? [default true]
     */
    boolean query() default true;

    /**
     * May URI contain fragment? [default true]
     */
    boolean fragment() default true;

    /**
     * The scheme(s) (protocol) the string must match, eg. ftp or http.
     * [default any]
     */
    String[] scheme() default {};
}
