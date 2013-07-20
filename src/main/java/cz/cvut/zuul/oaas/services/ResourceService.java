package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.zuul.oaas.models.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Validated
public interface ResourceService {

    /**
     * @param id resource's id
     * @return resource if exists
     * @throws NoSuchResourceException when no matching resource was found
     */
    Resource findResourceById(String id) throws NoSuchResourceException;
    
    /**
     * @param resource resource to be created
     * @return id of the created resource
     */
    String createResource(@Valid Resource resource);
    
    /**
     * @param id id of the resource to be updated
     * @param resource resource's content to be updated with
     * @throws NoSuchResourceException when no matching resource was found
     */
    void updateResource(String id, @Valid Resource resource) throws NoSuchResourceException;

    /**
     * @param id id of the resource to be deleted
     * @throws NoSuchResourceException when no matching resource was found
     */
    void deleteResourceById(String id) throws NoSuchResourceException;
    
    /**
     * @return all resources
     */
    List<Resource> getAllResources();

    /**
     * @return all public resources
     */
    List<Resource> getAllPublicResources();
}
