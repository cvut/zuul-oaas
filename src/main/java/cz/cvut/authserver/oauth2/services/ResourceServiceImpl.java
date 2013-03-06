package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.dao.ResourceDAO;
import cz.cvut.authserver.oauth2.models.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

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
    public boolean isRegisteredResource(String id) {
        return resourceDAO.exists(id);
    }

    @Override
    public List<Resource> getAllResources() {
        return newArrayList(resourceDAO.findAll());
    }

    @Override
    public List<Resource> getAllPublicResources() {
        return resourceDAO.findAllPublic();
    }

    @Override
    public Resource createResource(Resource resource) {
        LOG.info("Creating new resource [{}]", resource);
        return resourceDAO.save(resource);
    }

    @Override
    public void updateResource(String id, Resource resource) throws NoSuchResourceException{
        LOG.info("Updating resource [{}]", resource);

        assertResourceExists(id);
        resourceDAO.save(resource);
    }

    @Override
    public Resource findResourceById(String id) throws NoSuchResourceException {
        Resource resource = resourceDAO.findOne(id);

        if (resource == null) {
            throw new NoSuchResourceException("No such resource with id = " + id);
        }
        return resource;
    }

    @Override
    public void deleteResourceById(String id) throws NoSuchResourceException {
        assertResourceExists(id);
        resourceDAO.delete(id);
    }


    private void assertResourceExists(String resourceId) {
        if (! resourceDAO.exists(resourceId)) {
            throw new NoSuchResourceException("No such resource with id = " + resourceId);
        }
    }

    //////////  Getters / Setters  //////////
    
    public ResourceDAO getResourceDAO() {
        return resourceDAO;
    }

    public void setResourceDAO(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }
    
}
