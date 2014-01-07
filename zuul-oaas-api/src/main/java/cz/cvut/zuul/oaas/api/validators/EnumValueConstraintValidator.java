/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.api.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * JSR-303 validator for {@link EnumValue} constraint.
 */
public class EnumValueConstraintValidator implements ConstraintValidator<EnumValue, String> {

    private boolean nullable;
    private boolean caseSensitive;
    private List<String> values;


    public void initialize(EnumValue constraint) {
        Class<? extends Enum> clazz = constraint.value();
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
