package cz.cvut.zuul.oaas.dao.mongo;

import cz.cvut.zuul.oaas.dao.RefreshTokenDAO;
import cz.cvut.zuul.oaas.models.PersistableRefreshToken;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoRefreshTokenDAO
        extends AbstractMongoGenericDAO<PersistableRefreshToken, String> implements RefreshTokenDAO {


    public void deleteByClientId(String clientId) {
        mongo().remove(query(
                where("authentication.authorization_request.client_id").is(clientId)),
                entityClass());
    }
}
