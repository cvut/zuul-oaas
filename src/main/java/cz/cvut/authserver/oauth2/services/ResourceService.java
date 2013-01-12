package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.models.resource.Resource;
import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface ResourceService {

    public void createResource(Resource resource);
    
    public List<Resource> getAllResources();
}
