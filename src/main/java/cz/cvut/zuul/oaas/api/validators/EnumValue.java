package cz.cvut.zuul.oaas.api.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * JSR-303 constraint for String validation against given enumeration class.
 * 
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, ANNOTATION_TYPE})
@Constraint(validatedBy = EnumValueConstraintValidator.class)
public @interface EnumValue {

    String message() default "{validator.ValidEnum.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


    /**
     * Enumeration class to validate values against.
     */
    Class<? extends java.lang.Enum<?>> value();

    /**
     * Consider <tt>null</tt> as valid value? [default false]
     */
    boolean nullable() default false;

    /**
     * Case-sensitive comparison? [default true]
     */
    boolean caseSensitive() default true;
}
