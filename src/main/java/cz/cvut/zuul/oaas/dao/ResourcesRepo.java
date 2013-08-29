package cz.cvut.zuul.oaas.dao;

import cz.cvut.zuul.oaas.models.Resource;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface ResourcesRepo extends CrudRepository<Resource, String> {

    /**
     * Returns all resources.
     * 
     * @return all resources
     */
    List<Resource> findAllPublic();
}
