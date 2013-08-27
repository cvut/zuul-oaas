package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.models.ResourceDTO;
import cz.cvut.zuul.oaas.api.resources.exceptions.NoSuchResourceException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Validated
public interface ResourcesService {

    /**
     * @param id resource's id
     * @return resource if exists
     * @throws NoSuchResourceException when no matching resource was found
     */
    ResourceDTO findResourceById(String id) throws NoSuchResourceException;
    
    /**
     * @param resource resource to be created
     * @return id of the created resource
     */
    String createResource(@Valid ResourceDTO resource);
    
    /**
     * @param resource resource's content to be updated with
     * @throws NoSuchResourceException when no matching resource was found
     */
    void updateResource(@Valid ResourceDTO resource) throws NoSuchResourceException;

    /**
     * @param id id of the resource to be deleted
     * @throws NoSuchResourceException when no matching resource was found
     */
    void deleteResourceById(String id) throws NoSuchResourceException;
    
    /**
     * @return all resources
     */
    List<ResourceDTO> getAllResources();

    /**
     * @return all public resources
     */
    List<ResourceDTO> getAllPublicResources();
}
