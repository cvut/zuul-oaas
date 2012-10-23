package cz.cvut.authserver.oauth2.api.validation;

import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import cz.cvut.authserver.oauth2.api.validators.SecretChangeRequestValidator;
import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static org.junit.Assert.*;
import org.springframework.validation.FieldError;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class SecretChangeRequestValidatorTest {

    private BindingResult bindingResult;
    private SecretChangeRequestValidator validator;
    private SecretChangeRequest request;

    @Before
    public void init() {
        validator = new SecretChangeRequestValidator();
        request = new SecretChangeRequest();
        bindingResult = new DataBinder(request).getBindingResult();
    }

    @Test
    public void validate_request_with_empty_old_secret() {
        request.setNewSecret("123");
        request.setOldSecret("");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'newSecret' field found", bindingResult.getFieldError("newSecret"));
        assertNotNull("Error for 'oldSecret' field not found", bindingResult.getFieldError("oldSecret"));
    }

    @Test
    public void validate_request_with_empty_new_secret() {
        request.setOldSecret("123");
        request.setNewSecret("");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'oldSecret' field found", bindingResult.getFieldError("oldSecret"));
        assertNotNull("Error for 'newSecret' field not found", bindingResult.getFieldError("newSecret"));
    }

    @Test
    public void validate_empty_request() {
        request.setOldSecret("");
        request.setNewSecret("");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 2, bindingResult.getFieldErrorCount());
        assertNotNull("Error for 'oldSecret' field not found", bindingResult.getFieldError("oldSecret"));
        assertNotNull("Error for 'newSecret' field not found", bindingResult.getFieldError("newSecret"));
    }

    @Test
    public void vaidate_filled_request() {
        request.setOldSecret("123");
        request.setNewSecret("456");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertFalse("There are some field errors during validation", bindingResult.hasFieldErrors());
    }

    @Test
    public void validate_request_with_old_secret_value_length_exceeded() {
        String exceeded = getAsciiPrintableStringWithSize(333);
        request.setOldSecret(exceeded);
        request.setNewSecret("123");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'newSecret' field found", bindingResult.getFieldError("newSecret"));
        assertNotNull("Error for 'oldSecret' field not found", bindingResult.getFieldError("oldSecret"));
    }

    @Test
    public void validate_request_with_new_secret_value_length_exceeded() {
        String exceeded = getAsciiPrintableStringWithSize(333);
        request.setNewSecret(exceeded);
        request.setOldSecret("123");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'oldSecret' field found", bindingResult.getFieldError("oldSecret"));
        assertNotNull("Error for 'newSecret' field not found", bindingResult.getFieldError("newSecret"));
    }

    @Test
    public void validate_request_with_invalid_characters_in_old_secret() {
        String invalidCharacter = "\n";
        request.setOldSecret("123".concat(invalidCharacter));
        request.setNewSecret("456");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'newSecret' field found", bindingResult.getFieldError("newSecret"));
        assertNotNull("Error for 'oldSecret' field not found", bindingResult.getFieldError("oldSecret"));
    }

    @Test
    public void validate_request_with_invalid_characters_in_new_secret() {
        String invalidCharacter = "\n";
        request.setOldSecret("456");
        request.setNewSecret("123".concat(invalidCharacter));
        validator.validate(request, bindingResult);
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
