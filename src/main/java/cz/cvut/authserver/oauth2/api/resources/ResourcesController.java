package cz.cvut.authserver.oauth2.api.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Controller
@RequestMapping(value = "/v1/resources")
public class ResourcesController {
    
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesController.class);
    private String apiVersion;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    
}
