package cz.cvut.zuul.oaas.repos;

import cz.cvut.zuul.oaas.models.Resource;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResourcesRepo extends CrudRepository<Resource, String> {

    List<Resource> findAllPublic();
}
