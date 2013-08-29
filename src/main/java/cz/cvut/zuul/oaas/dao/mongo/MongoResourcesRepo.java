package cz.cvut.zuul.oaas.dao.mongo;

import cz.cvut.zuul.oaas.dao.ResourcesRepo;
import cz.cvut.zuul.oaas.models.Resource;
import cz.cvut.zuul.oaas.models.enums.Visibility;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoResourcesRepo extends AbstractMongoRepository<Resource, String> implements ResourcesRepo {


    public List<Resource> findAllPublic() {
        return mongo().find(query(
                where("visibility").is(Visibility.PUBLIC)),
                entityClass()
        );
    }
}
