package cz.cvut.authserver.oauth2.dao;

import cz.cvut.authserver.oauth2.models.Resource;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface ResourceDAO extends CrudRepository<Resource, String> {

    /**
     * Returns all resources.
     * 
     * @return all resources
     */
    List<Resource> findAllPublic();
}
