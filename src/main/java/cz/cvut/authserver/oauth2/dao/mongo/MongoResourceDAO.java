package cz.cvut.authserver.oauth2.dao.mongo;

import cz.cvut.authserver.oauth2.dao.ResourceDAO;
import cz.cvut.authserver.oauth2.models.Resource;
import cz.cvut.authserver.oauth2.models.enums.Visibility;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoResourceDAO extends AbstractMongoGenericDAO<Resource, String> implements ResourceDAO {


    public List<Resource> findAllPublic() {
        return mongo().find(query(
                where("visibility").is(Visibility.PUBLIC)),
                entityClass()
        );
    }
}
