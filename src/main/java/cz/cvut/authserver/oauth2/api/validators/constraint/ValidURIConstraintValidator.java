package cz.cvut.authserver.oauth2.api.validators.constraint;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * JSR-303 validator for {@link ValidURI} constraint.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ValidURIConstraintValidator implements ConstraintValidator <ValidURI, String> {

    private boolean relative;
    private boolean query;
    private boolean fragment;

    public void initialize(ValidURI constraint) {
        relative = constraint.relative();
        query = constraint.query();
        fragment = constraint.fragment();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            URI uri = new URI(value, false, "UTF-8");

            if (!relative && uri.isRelativeURI()
                    || !query &&  uri.getRawQuery() != null
                    || !fragment && uri.getRawFragment() != null
            ) return false;

        } catch (URIException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}
