package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.ErrorResponse;
import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import cz.cvut.authserver.oauth2.services.ResourceService;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * API for authorization server resource's management.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Controller
@RequestMapping(value = "/v1/resources")
public class ResourcesController {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesController.class);
    private String apiVersion;
    
    private ResourceService resourceService;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Resource> getAllResources() {
        return resourceService.getAllResources();
    }

    @ResponseBody
    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public List<Resource> getAllPublicResources() {
        return resourceService.getAllPublicResources();
    }
    
    @ResponseStatus(CREATED)
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Resource createResource(@RequestBody Resource resource) {
        Resource created = resourceService.createResource(resource);
        LOG.info("Creating new resource [{}]", resource);
        return created;
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteResource(@PathVariable String id) throws NoSuchResourceException {
        resourceService.deleteResourceById(id);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateResource(@PathVariable String id, @RequestBody Resource resource) throws NoSuchResourceException {
        resourceService.updateResource(id, resource);
    }
  
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource findResourceById(@PathVariable String id) throws NoSuchResourceException {
        return resourceService.findResourceById(id);
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

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    
}
