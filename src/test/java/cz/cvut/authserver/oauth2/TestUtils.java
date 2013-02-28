package cz.cvut.authserver.oauth2;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class TestUtils {

    private static ValidatorFactory VALIDATOR_FACTORY;


    public static ValidatorFactory getValidatorFactory() {
        if (VALIDATOR_FACTORY == null) {
            VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
        }
        return VALIDATOR_FACTORY;
    }

    public static Validator getValidator() {
        return getValidatorFactory().getValidator();
    }


    public static void assertValidProperty(Object object, String propertyName) {
        Set<ConstraintViolation<Object>> violations = getValidator().validateProperty(object, propertyName);

        if (!violations.isEmpty()) {
            Object value = violations.iterator().next().getInvalidValue();
            fail(String.format("Validator said that property %s is invalid for value: %s", propertyName, value));
        }
    }

    public static void assertInvalidProperty(Object object, String propertyName) {
        assertFalse(String.format(
                "Validator did not complain about property %s", propertyName),
                getValidator().validateProperty(object, propertyName).isEmpty());
    }
}
