package cz.cvut.authserver.oauth2.api.validation;

import com.google.common.collect.Sets;
import cz.cvut.authserver.oauth2.Factories;
import cz.cvut.authserver.oauth2.api.validators.ClientDetailsValidator;
import java.util.Set;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static org.junit.Assert.*;
import org.springframework.security.oauth2.provider.BaseClientDetails;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ClientDetailsValidatorTest {

    private BindingResult bindingResult;
    private ClientDetailsValidator validator;
    private BaseClientDetails clientDetails;

    @Before
    public void init() {
        validator = new ClientDetailsValidator();
        clientDetails = new BaseClientDetails();
        bindingResult = new DataBinder(clientDetails).getBindingResult();
    }

    @Test
    public void validate_with_empty_authGrantType() {
        //given

        //when
        validator.validate(clientDetails, bindingResult);

        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertNotNull("Error for 'authorizedGrantTypes' field not found", bindingResult.getFieldError("authorizedGrantTypes"));
    }

    @Test
    public void validate_invalid_authGrantType() {
        //given
        clientDetails.setAuthorizedGrantTypes(Factories.createInvalidAuthorizationGrant());

        //when
        validator.validate(clientDetails, bindingResult);

        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertNotNull("Error for 'authorizedGrantTypes' field not found", bindingResult.getFieldError("authorizedGrantTypes"));
        assertEquals("Wrong value was rejected.", Factories.createInvalidAuthorizationGrant(), bindingResult.getFieldError("authorizedGrantTypes").getRejectedValue());
    }

    @Test
    public void validate_authorization_code_with_empty_url() {
        //given
        clientDetails.setAuthorizedGrantTypes(Factories.createAuthorizationCodeGrant());

        //when
        validator.validate(clientDetails, bindingResult);

        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertNull("Error for 'authorizedGrantTypes' field found", bindingResult.getFieldError("authorizedGrantTypes"));
        assertNotNull("Error for 'registeredRedirectUri' field not found", bindingResult.getFieldError("registeredRedirectUri"));
    }

    @Test
    public void validate_authorization_code_with_INVALID_url() {
        //given
        clientDetails.setAuthorizedGrantTypes(Factories.createAuthorizationCodeGrant());
        clientDetails.setRegisteredRedirectUri(Sets.newHashSet("http://www.bad"));

        //when
        validator.validate(clientDetails, bindingResult);

        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertNull("Error for 'authorizedGrantTypes' field found", bindingResult.getFieldError("authorizedGrantTypes"));
        assertNotNull("Error for 'registeredRedirectUri' field not found", bindingResult.getFieldError("registeredRedirectUri"));
        assertEquals("Wrong value was rejected.", Sets.newHashSet("http://www.bad"), bindingResult.getFieldError("registeredRedirectUri").getRejectedValue());
    }

    @Test
    public void validate_authorization_code_with_VALID_url() {
        //given
        clientDetails.setAuthorizedGrantTypes(Factories.createAuthorizationCodeGrant());
        clientDetails.setRegisteredRedirectUri(Sets.newHashSet("http://www.good.com"));

        //when
        validator.validate(clientDetails, bindingResult);

        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertFalse("There are field errors during validation", bindingResult.hasFieldErrors());
    }
    
    @Test
    public void validate_ignore_emtpy_url_when_is_not_authorization_code_grant() {
        //given
        clientDetails.setAuthorizedGrantTypes(Factories.createImplicitGrant());

        //when
        validator.validate(clientDetails, bindingResult);

        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertFalse("There are field errors during validation", bindingResult.hasFieldErrors());
    }

    @Test
    public void validate_ignore_INVALID_url_when_is_not_authorization_code_grant() {
        //given
        clientDetails.setAuthorizedGrantTypes(Factories.createImplicitGrant());
        clientDetails.setRegisteredRedirectUri(Sets.newHashSet("http://www.good.com"));

        //when
        validator.validate(clientDetails, bindingResult);

        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertFalse("There are field errors during validation", bindingResult.hasFieldErrors());
    }
    

    @Test
    public void validate_url_length_exceeded() {
        //given
        String exceeded = Factories.randomAsciiPrintableStringWithSize(333);
        clientDetails.setAuthorizedGrantTypes(Factories.createAuthorizationCodeGrant());
        Set uris = Sets.newHashSet(exceeded);
        clientDetails.setRegisteredRedirectUri(uris);
        
        //when
        validator.validate(clientDetails, bindingResult);
        
        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertNull("Error for 'authorizedGrantTypes' field found", bindingResult.getFieldError("authorizedGrantTypes"));
        assertNotNull("Error for 'registeredRedirectUri' field not found", bindingResult.getFieldError("registeredRedirectUri"));
        assertEquals("Wrong value was rejected.", uris, bindingResult.getFieldError("registeredRedirectUri").getRejectedValue());
    }

    @Test
    public void validate_auth_grant_length_exceeded() {
        //given
        String exceeded = Factories.randomAsciiPrintableStringWithSize(333);
        Set grants = Sets.newHashSet(exceeded);
        clientDetails.setAuthorizedGrantTypes(grants);
        
        //when
        validator.validate(clientDetails, bindingResult);
        
        //then
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertNull("Error for 'registeredRedirectUri' field found", bindingResult.getFieldError("registeredRedirectUri"));
        assertNotNull("Error for 'authorizedGrantTypes' field not found", bindingResult.getFieldError("authorizedGrantTypes"));
        assertEquals("Wrong value was rejected.", grants, bindingResult.getFieldError("authorizedGrantTypes").getRejectedValue());
    }

    @Test
    public void validate_request_with_invalid_characters_in_old_secret() {
        //given
        String invalidCharacter = "\n";
        
        //when
        
        
        //then

//        clientDetails.setOldSecret("123".concat(invalidCharacter));
//        clientDetails.setNewSecret("456");
        validator.validate(clientDetails, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'newSecret' field found", bindingResult.getFieldError("newSecret"));
        assertNotNull("Error for 'oldSecret' field not found", bindingResult.getFieldError("oldSecret"));
    }

    @Test
    public void validate_request_with_invalid_characters_in_new_secret() {
        String invalidCharacter = "\n";
//        clientDetails.setOldSecret("456");
//        clientDetails.setNewSecret("123".concat(invalidCharacter));
        validator.validate(clientDetails, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'oldSecret' field found", bindingResult.getFieldError("oldSecret"));
        assertNotNull("Error for 'newSecret' field not found", bindingResult.getFieldError("newSecret"));
    }

    // TODO move to cz.cvut.authserver.oauth2.Factories ?
    private String getAsciiPrintableStringWithSize(int size) {
        return RandomStringUtils.randomAscii(size);
    }
}
