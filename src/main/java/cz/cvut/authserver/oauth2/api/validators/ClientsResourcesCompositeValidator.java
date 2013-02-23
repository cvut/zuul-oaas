package cz.cvut.authserver.oauth2.api.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ClientsResourcesCompositeValidator implements Validator {
    
    public static final Logger LOG = LoggerFactory.getLogger(ClientsResourcesCompositeValidator.class);

    private Validator[] validators;

    /**
     * Will return true if this class is in the specified map.
     */
    @Override
    public boolean supports(final Class clazz) {
        for (Validator v : validators) {
            if (v.supports(clazz)) {
                LOG.debug("Validation for class [{}] is supported by [{}]", clazz, v);
                return true;
            }
        }
        return false;
    }

    /**
     * Validate the specified object using the validator registered for the
     * object's class.
     */
    @Override
    public void validate(final Object obj, final Errors errors) {
        
        for (Validator v : validators) {
            if (v.supports(obj.getClass())) {
                v.validate(obj, errors);
                LOG.debug("Exiting validation exection with validator [{}] for class [{}] with errors: {}", new Object[]{v, obj.getClass(), errors});
//                return;
            }
        }
    }

    public void setValidators(Validator[] validators) {
        LOG.debug("Setting validators: {}", validators);
        this.validators = validators.clone();
    }

}
