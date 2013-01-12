package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.dao.ResourceDAO;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@org.springframework.stereotype.Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;

    @Override
    public List<Resource> getAllResources() {
        return resourceDAO.getAllResources();
    }

    @Override
    public void createResource(Resource resource) {
        resourceDAO.createResource(resource);
    }

    //////////  Getters / Setters  //////////
    
    public ResourceDAO getResourceDAO() {
        return resourceDAO;
    }

    public void setResourceDAO(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }
    
}
