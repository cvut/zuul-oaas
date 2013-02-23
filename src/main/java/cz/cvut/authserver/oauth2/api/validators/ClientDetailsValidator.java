package cz.cvut.authserver.oauth2.api.validators;

import cz.cvut.authserver.oauth2.models.AuthorizationGrants;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Validator for spring oauth2 ClientDetails. Validating required attributes,
 * attribute's length and that attributes contains only the ascii printable
 * characters.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ClientDetailsValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ClientDetailsValidator.class);
    private static final int MAX_VALUE_LENGTH = 256;
    
    private UrlValidator urlValidator = new UrlValidator();

    @Override
    public boolean supports(Class<?> clazz) {
        return ClientDetails.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ClientDetails clientDetails = (ClientDetails) target;

        Set<String> registeredRedirectUri = clientDetails.getRegisteredRedirectUri();
        Set<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        
        // #1 : validate required fields
        if (isEmpty(authorizedGrantTypes)) {
            errors.rejectValue("authorizedGrantTypes", "argument.required", new Object[]{"authorized_grant_types"}, "Argument 'authorized_grant_types' is required");
            return;
        }
        
        boolean urlValidationRequired = isRedirectURIsValidationNecessary(authorizedGrantTypes);

        
        if (urlValidationRequired && isEmpty(registeredRedirectUri)) {
            errors.rejectValue("registeredRedirectUri", "argument.required", new Object[]{"redirect_uri"}, "Argument 'redirect_uri' is required");
            return;
        }

        // #2: check parameters length, otherwise will cause the internal server error: org.postgresql.util.PSQLException: ERROR: value too long for type character varying(256)
        if (urlValidationRequired && isValueTooLong(registeredRedirectUri)) {
            errors.rejectValue("registeredRedirectUri", "argument.value.too.long", new Object[]{"redirect_uri", MAX_VALUE_LENGTH}, "Argument value too long");
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
        String invalidGrant = retrieveInvalidGrantType(authorizedGrantTypes);
        if (invalidGrant != null) {
            errors.rejectValue("authorizedGrantTypes", "invalid.grant.type", new Object[]{invalidGrant}, String.format("Grant type %s is invalid", invalidGrant));
            return;
        }

        // #4 : validate if the given url is valid
        if (urlValidationRequired && !isValidUrl(registeredRedirectUri)) {
            errors.rejectValue("registeredRedirectUri", "invalid.url");
            return;
        }
        
    }

    
    private boolean isRedirectURIsValidationNecessary(Set<String> args){
        if (isEmpty(args)) {
            return false;
        }
        for (String string : args) {
            if (AuthorizationGrants.auth_code.toString().equals(string)) {
                return true;
            }
        }
        return false;
    }

    
    private String retrieveInvalidGrantType(Set<String> args) {
        for (String string : args) {
            if (! AuthorizationGrants.contains(string)) {
                return string;
            }
        }
        return null;
    }

    private boolean isValueTooLong(Set<String> args) {
        int total = 0;
        for (String string : args) {
            total = total + string.length();
        }
        return total > MAX_VALUE_LENGTH;
    }

    /**
     * Condition required by
     * {@see http://tools.ietf.org/html/draft-ietf-oauth-v2-31#appendix-A.2}.
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

    private boolean isValidUrl(Set<String> urls){
        for (String string : urls) {
            if (!urlValidator.isValid(string)) {
                return false;
            }
        }
        return true;
    }
}
