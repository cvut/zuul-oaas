package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.dao.ResourceDAO;
import cz.cvut.authserver.oauth2.generators.IdentifierGenerator;
import cz.cvut.authserver.oauth2.models.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private @Autowired ResourceDAO resourceDAO;
    private @Autowired IdentifierGenerator identifierGenerator;



    @Override
    public List<Resource> getAllResources() {
        return newArrayList(resourceDAO.findAll());
    }

    @Override
    public List<Resource> getAllPublicResources() {
        return resourceDAO.findAllPublic();
    }

    @Override
    public String createResource(Resource resource) {
        String resourceId;
        do {
            LOG.debug("Generating unique resourceId");
            resourceId = identifierGenerator.generateArgBasedIdentifier(resource.getName());
        } while (resourceDAO.exists(resourceId));

        resource.setId(resourceId);

        LOG.info("Creating new resource: [{}]", resource);
        resourceDAO.save(resource);

        return resourceId;
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


    //////////  Accessors  //////////

    public void setResourceDAO(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }

    public void setIdentifierGenerator(IdentifierGenerator identifierGenerator) {
        this.identifierGenerator = identifierGenerator;
    }
}
