package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.generators.OAuth2ClientCredentialsGenerator;
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
import org.springframework.web.bind.annotation.RequestBody;

/**
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
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void createResource(@RequestBody Resource resource) {
        resource.setId(Long.MIN_VALUE);
        resourceService.createResource(resource);
        LOG.info("Creating new resource [{}]", resource);
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
