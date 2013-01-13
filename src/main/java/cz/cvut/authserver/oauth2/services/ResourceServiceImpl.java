package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
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

    @Override
    public void updateResource(Long id, Resource resource) throws NoSuchResourceException{
        resourceDAO.updateResource(id, resource);
    }

    @Override
    public Resource findResourceById(Long id) throws NoSuchResourceException{
        return resourceDAO.findResourceById(id);
    }

    @Override
    public void deleteResourceById(Long id) throws NoSuchResourceException{
        resourceDAO.deleteResourceById(id);
    }

    //////////  Getters / Setters  //////////
    
    public ResourceDAO getResourceDAO() {
        return resourceDAO;
    }

    public void setResourceDAO(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }
    
}
