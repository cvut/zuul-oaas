package cz.cvut.zuul.oaas.repos.mongo;

import cz.cvut.zuul.oaas.repos.ResourcesRepo;
import cz.cvut.zuul.oaas.models.Resource;
import cz.cvut.zuul.oaas.models.Visibility;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class MongoResourcesRepo extends AbstractMongoRepository<Resource, String> implements ResourcesRepo {


    public List<Resource> findAllPublic() {
        return mongo().find(query(
                where("visibility").is(Visibility.PUBLIC)),
                entityClass()
        );
    }
}
