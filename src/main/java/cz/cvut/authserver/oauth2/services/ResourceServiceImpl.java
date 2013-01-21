package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.dao.ResourceDAO;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import java.io.Serializable;
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
    public boolean isRegisteredResource(Serializable id) {
        return resourceDAO.isRegisteredResource(id);
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceDAO.getAllResources();
    }

    @Override
    public List<Resource> getAllPublicResources() {
        return resourceDAO.getAllPublicResources();
    }

    @Override
    public Resource createResource(Resource resource) {
        return resourceDAO.createResource(resource);
    }

    @Override
    public void updateResource(String id, Resource resource) throws NoSuchResourceException{
        resourceDAO.updateResource(id, resource);
    }

    @Override
    public Resource findResourceById(String id) throws NoSuchResourceException{
        return resourceDAO.findResourceById(id);
    }

    @Override
    public void deleteResourceById(String id) throws NoSuchResourceException{
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
