package cz.cvut.zuul.oaas.api.validators;

import cz.jirutka.validator.collection.CommonEachValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * JSR-303 collection wrapper constraint for {@link ValidURI}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER})
@Constraint(validatedBy = CommonEachValidator.class)
public @interface EachEnum {

    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    EnumValue[] value();
}
