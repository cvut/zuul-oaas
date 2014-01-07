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
package cz.cvut.zuul.oaas.api.test

import javax.validation.Validation
import javax.validation.Validator

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
