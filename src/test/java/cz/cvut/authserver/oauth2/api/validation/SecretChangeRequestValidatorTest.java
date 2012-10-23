package cz.cvut.authserver.oauth2.api.validation;

import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import cz.cvut.authserver.oauth2.api.validators.SecretChangeRequestValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import static org.junit.Assert.*;

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
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'oldSecret' field found", bindingResult.getFieldError("oldSecret"));
        assertNotNull("Error for 'newSecret' field not found", bindingResult.getFieldError("newSecret"));
    }

    @Test
    public void validate_empty_request() {
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
        String exceeded = getStringWithSize(333);
        request.setOldSecret(exceeded);
        request.setNewSecret("123");
        validator.validate(request, bindingResult);
        System.out.println(">>>>" + bindingResult.hasErrors() + bindingResult.hasFieldErrors());
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'newSecret' field found", bindingResult.getFieldError("newSecret"));
        assertNotNull("Error for 'oldSecret' field not found", bindingResult.getFieldError("oldSecret"));
    }

    @Test
    public void validate_request_with_new_secret_value_length_exceeded() {
        String exceeded = getStringWithSize(333);
        request.setNewSecret(exceeded);
        request.setOldSecret("123");
        validator.validate(request, bindingResult);
        assertFalse("There are some global errors during validation", bindingResult.hasGlobalErrors());
        assertTrue("There aren't field errors during validation", bindingResult.hasFieldErrors());
        assertEquals("Unexpected field errors count", 1, bindingResult.getFieldErrorCount());
        assertNull("Error for 'oldSecret' field found", bindingResult.getFieldError("oldSecret"));
        assertNotNull("Error for 'newSecret' field not found", bindingResult.getFieldError("newSecret"));
    }

    // TODO move to cz.cvut.authserver.oauth2.Factories ?
    private String getStringWithSize(int size) {
        assert size > 0 && size < Integer.MAX_VALUE;
        char[] array = new char[size];
        return new String(array);
    }
}
