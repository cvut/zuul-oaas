package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.zuul.oaas.dao.ResourceDAO;
import cz.cvut.zuul.oaas.generators.IdentifierGenerator;
import cz.cvut.zuul.oaas.models.Resource;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Setter
@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private ResourceDAO resourceDAO;
    private IdentifierGenerator identifierGenerator;



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
}
