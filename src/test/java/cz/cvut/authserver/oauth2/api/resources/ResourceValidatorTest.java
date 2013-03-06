package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.Factories;
import cz.cvut.authserver.oauth2.models.Resource;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.PropertyDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration ("classpath:/api-context-test.xml")
public class ResourceValidatorTest {
    
    @Autowired
    private Validator validator;
    
    @Test
    public void test_validator_setup_for_resource_entity(){
        // We want to ensure that the all validators are properly registered
        
        // if we wouldn't have used depndency injection we can get access to validator like in the following code:
        //        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        //        Validator validator = validatorFactory.getValidator();
        
        //given
        //when
        PropertyDescriptor baseUrlDescriptor = validator.getConstraintsForClass(Resource.class).getConstraintsForProperty("baseUrl");
        PropertyDescriptor visibilityDescriptor = validator.getConstraintsForClass(Resource.class).getConstraintsForProperty("visibility");
        
        //then
        assertTrue("No validation is involved.", validator.getConstraintsForClass(Resource.class).isBeanConstrained());
        assertFalse("No constrained properties for Resource.class", validator.getConstraintsForClass(Resource.class).getConstrainedProperties().isEmpty());
        assertNotNull("No constraints for property 'baseUrl'", baseUrlDescriptor);
        assertNotNull("No constraints for property 'visibility'", visibilityDescriptor);

        // uncomment to display connstraint annotation for the given field
        //        for (ConstraintDescriptor cd : validator.getConstraintsForClass(Resource.class).getConstraintsForProperty("baseUrl").getConstraintDescriptors()){
        //        }
    }

    @Test
    public void validate_missing_baseUrl() {
        Resource toCreate = Factories.createResource();
        toCreate.setBaseUrl(null);
        Set<ConstraintViolation<Resource>> violations = validator.validate(toCreate, javax.validation.groups.Default.class);
        // uncomment to display invalid values with validations messages
        //        for (ConstraintViolation<Resource> constraintViolation : violations) {
        //            System.out.println("invalid val: " + constraintViolation.getInvalidValue());
        //            System.out.println("mess: " + constraintViolation.getMessage());
        //        }
        assertFalse("No violations registered.", violations.isEmpty());
    }

    @Test
    public void validate_missing_visibility() {
        Resource toCreate = Factories.createResource();
        toCreate.setVisibility(null);
        Set<ConstraintViolation<Resource>> violations = validator.validate(toCreate, javax.validation.groups.Default.class);
        // uncomment to display invalid values with validations messages
        //        for (ConstraintViolation<Resource> constraintViolation : violations) {
        //            System.out.println("invalid val: " + constraintViolation.getInvalidValue());
        //            System.out.println("mess: " + constraintViolation.getMessage());
        //        }
        assertFalse("No violations registered.", violations.isEmpty());
    }

    @Test
    public void validate_invalid_baseUrl() {
        Resource toCreate = Factories.createResource();
        toCreate.setBaseUrl("wwww.baaaad_wrrrr");
        Set<ConstraintViolation<Resource>> violations = validator.validate(toCreate, javax.validation.groups.Default.class);
        assertFalse("No violatons registered.", violations.isEmpty());
    }

    @Test
    public void validate_invalid_visibility() {
        Resource toCreate = Factories.createResource();
        toCreate.setVisibility("BAAANG");
        Set<ConstraintViolation<Resource>> violations = validator.validate(toCreate, javax.validation.groups.Default.class);
        assertFalse("No violatons registered.", violations.isEmpty());
    }
    
    @Test
    public void validate_valid_resource() {
        Resource toCreate = Factories.createResource();
        Set<ConstraintViolation<Resource>> violations = validator.validate(toCreate, javax.validation.groups.Default.class);
        assertTrue("Violatons was registered.", violations.isEmpty());
    }
}
