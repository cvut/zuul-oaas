package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.utils.AuthorizationGrants;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for spring oauth2 ClientDetails. Validating required attributes,
 * attribute's lentgh and that attributes contains only the ascii printable
 * characters.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ClientDetailsValidator implements Validator {

    public static final Logger LOG = LoggerFactory.getLogger(ClientDetailsValidator.class);
    private static final int MAX_VALUE_LENGTH = 256;
    
    UrlValidator urlValidator = new UrlValidator();

    @Override
    public boolean supports(Class<?> clazz) {
        return ClientDetails.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // first convert target to ClientDetails
        ClientDetails clientDetails = (ClientDetails) target;

        Set<String> registeredRedirectUri = clientDetails.getRegisteredRedirectUri();
        Set<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        
        // #1 : validate required fields
        if (isEmptyOrNull(authorizedGrantTypes)) {
            errors.rejectValue("authorizedGrantTypes", "argument.required", new Object[]{"authorized_grant_types"}, "Argument 'authorized_grant_types' is required");
            return;
        }
        
        boolean urlValidationRequired = isRedirectURIsValidationNecessary(authorizedGrantTypes);

        
        if (urlValidationRequired && isEmptyOrNull(registeredRedirectUri)) {
            errors.rejectValue("registeredRedirectUri", "argument.required", new Object[]{"redirect_uri"}, "Argument 'redirect_uri' is required");
            return;
        }

        // #2: check parameters length, otherwise will cause the internal server error: org.postgresql.util.PSQLException: ERROR: value too long for type character varying(256)
        if (urlValidationRequired && isValueTooLong(registeredRedirectUri)) {
            errors.rejectValue("registeredRedirectUri", "argument.value.too.long", new Object[]{"redirect_uri", MAX_VALUE_LENGTH}, "Argument value too long");
            return;
        }
        if (isValueTooLong(authorizedGrantTypes)) {
            errors.rejectValue("authorizedGrantTypes", "argument.value.too.long", new Object[]{"authorized_grant_types", MAX_VALUE_LENGTH}, "Argument value too long");
            return;
        }

//        // #3 : validate that attributes contains only the ascii printable characters
//        if (urlValidationRequired && containsInvalidCharacters(registeredRedirectUri)) {
//            errors.rejectValue("registeredRedirectUri", "argument.contains.invalid.characters", new Object[]{"redirect_uri"}, "Attribute contains invalid characters");
//            return;
//        }
//        if (containsInvalidCharacters(authorizedGrantTypes)) {
//            errors.rejectValue("authorizedGrantTypes", "argument.contains.invalid.characters", new Object[]{"authorized_grant_types"}, "Attribute contains invalid characters");
//            return;
//        }
        
        // #3 : validate if it has valid grant type
        String invalidGrant = retriveInvalidGrantType(authorizedGrantTypes);
        if (invalidGrant != null) {
            errors.rejectValue("authorizedGrantTypes", "invalid.grant.type", new Object[]{}, String.format("Grant type %s is invalid", invalidGrant));
            return;
        }

        // #4 : validate if the given url is valid
        if (urlValidationRequired && !isValidUrl(registeredRedirectUri)) {
            errors.rejectValue("registeredRedirectUri", "invalid.url");
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
    
    
    private boolean isRedirectURIsValidationNecessary(Set<String> args){
        if (isEmptyOrNull(args)) {
            return false;
        }
        for (String string : args) {
            if (string.equals(AuthorizationGrants.authCode.get())) {
                return true;
            }
        }
        return false;
    }
    
    private String retriveInvalidGrantType(String arg) {
        if (arg.equals(AuthorizationGrants.authCode.get())) {
            return null;
        }
        if (arg.equals(AuthorizationGrants.implict.get())) {
            return null;
        }
        if (arg.equals(AuthorizationGrants.clientCredentials.get())) {
            return null;
        }
        if (arg.equals(AuthorizationGrants.clientCredentials.get())) {
            return null;
        }
        return arg;
    }
    
    private String retriveInvalidGrantType(Set<String> args) {
        for (String string : args) {
            if (retriveInvalidGrantType(string)!=null) {
                return string;
            }
        }
        return null;
    }

    private boolean isValueTooLong(String arg) {
        return arg.length() > MAX_VALUE_LENGTH;
    }

    private boolean isValueTooLong(Set<String> args) {
        if (args==null ) {
            
        }
        int total = 0;
        for (String string : args) {
            total = total + string.length();
        }
        return total > MAX_VALUE_LENGTH;
    }

    /**
     * Condition required by
     * {@link http://tools.ietf.org/html/draft-ietf-oauth-v2-31#appendix-A.2}.
     *
     */
    private boolean containsInvalidCharacters(String arg) {
        return !StringUtils.isAsciiPrintable(arg);
    }
    
    private boolean containsInvalidCharacters(Set<String> args){
        for (String string : args) {
            if (containsInvalidCharacters(string)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidUrl(String url){
        return urlValidator.isValid(url);
    }

    private boolean isValidUrl(Set<String> urls){
        for (String string : urls) {
            if (!isValidUrl(string)) {
                return false;
            }
        }
        return true;
    }
}
