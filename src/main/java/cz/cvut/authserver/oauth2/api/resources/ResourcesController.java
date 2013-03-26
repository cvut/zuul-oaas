package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.ErrorResponse;
import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.models.Resource;
import cz.cvut.authserver.oauth2.services.ResourceService;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * API for authorization server resource's management.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Controller
@RequestMapping("/v1/resources/")
public class ResourcesController {

    private static final String SELF_URI = "/v1/resources/";

    private ResourceService resourceService;


    @RequestMapping(method = GET)
    public @ResponseBody List<Resource> getAllResources() {
        return resourceService.getAllResources();
    }

    @RequestMapping(value = "/public", method = GET)
    public @ResponseBody List<Resource> getAllPublicResources() {
        return resourceService.getAllPublicResources();
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = GET)
    public Resource getResource(@PathVariable String id) {
        return resourceService.findResourceById(id);
    }

    @ResponseStatus(CREATED)
    @RequestMapping(method = POST)
    public void createResource(@RequestBody Resource resource, HttpServletResponse response) {
        Resource created = resourceService.createResource(resource);

        // send redirect to URI of the created resource (i.e. api/resources/{id}/)
        response.setHeader("Location", SELF_URI + created.getId());
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = PUT)
    public void updateResource(@PathVariable String id, @RequestBody Resource resource) {
        resourceService.updateResource(id, resource);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = DELETE)
    public void deleteResource(@PathVariable String id) {
        resourceService.deleteResourceById(id);
    }


    //////////  Exceptions Handling  //////////
    
    @ExceptionHandler(NoSuchResourceException.class)
    public ResponseEntity<Void> handleNoSuchClient(NoSuchResourceException ex) {
        return new ResponseEntity<>(NOT_FOUND);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodConstraintViolationException.class)
    @SuppressWarnings("deprecation") // will be changed after JSR-349 release
    public @ResponseBody ErrorResponse handleValidationError(MethodConstraintViolationException ex) {
        return ErrorResponse.from(BAD_REQUEST, ex);
    }


    //////////  Getters / Setters  //////////

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
}
