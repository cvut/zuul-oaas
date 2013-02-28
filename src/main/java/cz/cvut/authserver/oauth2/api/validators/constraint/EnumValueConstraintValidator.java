package cz.cvut.authserver.oauth2.api.validators.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * JSR-303 validator for {@link EnumValue} constraint.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class EnumValueConstraintValidator implements ConstraintValidator<EnumValue, String> {

    private Class<? extends Enum> clazz;
    private boolean nullable;
    private boolean caseSensitive;
    private List<String> values;


    public void initialize(EnumValue constraint) {
        clazz = constraint.value();
        nullable = constraint.nullable();
        caseSensitive = constraint.caseSensitive();
        values = new ArrayList<>(clazz.getEnumConstants().length);

        for (Enum e : clazz.getEnumConstants()) {
            String value = e.toString();
            values.add(caseSensitive ? value : value.toUpperCase());
        }
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return nullable;
        } else {
            return values.contains(caseSensitive ? value : value.toUpperCase());
        }
    }
}
