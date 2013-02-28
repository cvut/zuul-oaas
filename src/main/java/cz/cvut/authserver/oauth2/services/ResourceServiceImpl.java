package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.dao.ResourceDAO;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);


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
        LOG.info("Creating new resource [{}]", resource);
        return resourceDAO.createResource(resource);
    }

    @Override
    public void updateResource(String id, Resource resource) throws NoSuchResourceException{
        LOG.info("Updating resource [{}]", resource);
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
