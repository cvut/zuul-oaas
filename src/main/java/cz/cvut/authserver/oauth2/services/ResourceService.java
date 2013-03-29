package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.models.Resource;
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
     * Finds resource with the given id.
     * 
     * @param id resource's id
     * @return resource if exists
     * @throws NoSuchResourceException when no matching resource was found
     */
    Resource findResourceById(String id) throws NoSuchResourceException;
    
    /**
     * Creates new resource.
     * 
     * @param resource resource to be created
     * @return resourceId
     */
    String createResource(@Valid Resource resource);
    
    /**
     * Update resource with the given id.
     * 
     * @param id resource's id
     * @param resource resource's content to be updated with
     * @throws NoSuchResourceException when no matching resource was found
     */
    void updateResource(String id, @Valid Resource resource) throws NoSuchResourceException;

    /**
     * Delete resource with the given id.
     * 
     * @param id resource§s id
     * @throws NoSuchResourceException when no matching resource was found
     */
    void deleteResourceById(String id) throws NoSuchResourceException;
    
    /**
     * Returns all resources.
     * 
     * @return all resources
     */
    List<Resource> getAllResources();

    /**
     * Returns all resources.
     *
     * @return all resources
     */
    List<Resource> getAllPublicResources();
}
