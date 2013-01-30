package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.services.ResourceService;
import cz.cvut.authserver.oauth2.utils.AuthorizationGrants;
import cz.cvut.authserver.oauth2.utils.RequestContextHolderUtils;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for Resources. Validating if it's not null or empty,
 * attribute's lentgh and that resource is registered resource in the Auth server.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ResourcesValidator implements Validator {

    public static final Logger LOG = LoggerFactory.getLogger(ResourcesValidator.class);
    private static final int MAX_VALUE_LENGTH = 256;

    private ResourceService resourceService;

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // first convert target to String
        String resource = (String) target;
        
        // Small hack:
        // SKIP this validation if it is unadequate (this step is determined by the current request URI)
        String requestURI = RequestContextHolderUtils.getRequestURI();
        if (!requestURI.endsWith("/resources")) {
            LOG.debug("Skipping Resources Validation because validation is not required according the request URI: [{}]", requestURI);
            return;
        }
        
        // #1 : validate required fields
        if (isEmptyOrNull(resource)) {
            errors.reject("argument.required", new Object[]{"resource_id"}, "Argument 'resource_id' is required");
            return;
        }
        
        // #2 : validate value too long
        if (isValueTooLong(resource)) {
            errors.reject("argument.value.too.long", new Object[]{"resource_id", MAX_VALUE_LENGTH}, "Argument value too long");
            return;
        }
        
        // #3 : validate if it has valid grant type
        String invalidResource = retriveInvalidResource(resource);
        if (invalidResource != null) {
            errors.reject("invalid.resource.id", new Object[]{invalidResource}, String.format("Resrource id %s is invalid", invalidResource));
            return;
        }
        
    }
    
    private boolean isEmptyOrNull(String arg){
        return !StringUtils.isNotEmpty(arg);
    }
    
    private boolean isEmptyOrNull(Set<String> args) {
        if (args == null) {
            return true;
        }
        if (args.isEmpty()) {
            return true;
        }
        for (String string : args) {
            if (isEmptyOrNull(string)) {
                return true;
            }
        }
        return false;
    }
    
    private String retriveInvalidResource(String arg) {
        return resourceService.isRegisteredResource(arg) ? null : arg;
    }
    
    private String retriveInvalidResource(Set<String> args) {
        for (String string : args) {
            if (retriveInvalidResource(string)!=null) {
                return string;
            }
        }
        return null;
    }

    private boolean isValueTooLong(String arg) {
        return arg.length() > MAX_VALUE_LENGTH;
    }

    //////////  Getters / Setters  //////////

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
}
