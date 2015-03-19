/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.api.validators

import org.apache.commons.httpclient.URI
import org.apache.commons.httpclient.URIException

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * JSR-303 validator for {@link ValidURI} constraint.
 */
class ValidURIConstraintValidator implements ConstraintValidator <ValidURI, String> {

    private boolean relative
    private boolean query
    private boolean fragment
    private String[] scheme

    void initialize(ValidURI constraint) {
        relative = constraint.relative()
        query = constraint.query()
        fragment = constraint.fragment()
        scheme = constraint.scheme()
    }

    boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false
        try {
            def uri = new URI(value, false, 'UTF-8')

            if (!relative && uri.relativeURI
                    || !query &&  uri.rawQuery
                    || !fragment && uri.rawFragment
                    || scheme && !(uri.scheme in scheme)
            ) return false

        } catch (URIException ex) {
            return false
        }
        return true
    }
}
