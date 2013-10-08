package cz.cvut.zuul.oaas.api.test

import javax.validation.Validation
import javax.validation.Validator

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ValidatorUtils {

    public static final valid = 'valid'
    public static final invalid = 'invalid'

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator()
    final Class<?> clazz


    private ValidatorUtils(Class<?> clazz) {
        this.clazz = clazz
    }

    static ValidatorUtils createValidator(Class<?> clazz) {
        new ValidatorUtils(clazz)
    }


    void isValid(Object object) {
        def violations = validator.validate(object)
        assert violations.isEmpty(), "Object should be valid, but validator said: ${violations*.message.join(", ")}"
    }

    void isInvalid(Object object) {
        def violations = validator.validate(object)
        assert !violations.isEmpty(), "Object should not pass validation"
    }

    void isValid(String propertyName, Object value) {
        def violations = validator.validateValue(clazz, propertyName, value)
        assert violations.isEmpty(), "Property should be valid, but validator said: ${violations*.message.join(", ")}"
    }

    void isInvalid(String propertyName, Object value) {
        def violations = validator.validateValue(clazz, propertyName, value)
        assert !violations.isEmpty(), "Property should not pass validation"
    }

    void validate(String propertyName, Object value, String expected) {
        (expected == invalid) ? isInvalid(propertyName, value) : isValid(propertyName, value)
    }
}
