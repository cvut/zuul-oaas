package cz.cvut.zuul.oaas.api.validators;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * JSR-303 validator for {@link ValidURI} constraint.
 */
public class ValidURIConstraintValidator implements ConstraintValidator <ValidURI, String> {

    private boolean relative;
    private boolean query;
    private boolean fragment;
    private String[] scheme;

    public void initialize(ValidURI constraint) {
        relative = constraint.relative();
        query = constraint.query();
        fragment = constraint.fragment();
        scheme = constraint.scheme();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        try {
            URI uri = new URI(value, false, "UTF-8");

            if (!relative && uri.isRelativeURI()
                    || !query &&  uri.getRawQuery() != null
                    || !fragment && uri.getRawFragment() != null
                    || !isEmpty(scheme) && !contains(scheme, uri.getScheme())
            ) return false;

        } catch (URIException ex) {
            return false;
        }
        return true;
    }
}
