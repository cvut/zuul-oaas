package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import cz.cvut.authserver.oauth2.services.ResourceService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * API for autorization server resource's management.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Controller
@RequestMapping(value = "/v1/resources")
public class ResourcesController {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesController.class);
    private String apiVersion;
    
    @Autowired
    private ResourceService resourceService;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Resource> getAllResources() {
        return resourceService.getAllResources();
    }
    
    @ResponseStatus(CREATED)
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Resource createResource(@RequestBody Resource resource) {
        resource.setId(Long.MIN_VALUE);
        Resource created = resourceService.createResource(resource);
        LOG.info("Creating new resource [{}]", resource);
        return created;
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteResource(@PathVariable Long id) throws Exception{
        resourceService.deleteResourceById(id);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateResource(@PathVariable Long id, @RequestBody Resource resource) throws Exception{
        resourceService.updateResource(id, resource);
    }
  
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource getAllResources(@PathVariable Long id) {
        return resourceService.findResourceById(id);
    }
    
    //////////  Exceptions Handling  //////////
    
    @ExceptionHandler(NoSuchResourceException.class)
    public ResponseEntity<Void> handleNoSuchClient(NoSuchResourceException ex) {
        return new ResponseEntity<>(NOT_FOUND);
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
