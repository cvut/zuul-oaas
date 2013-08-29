package cz.cvut.zuul.oaas.repos.mongo;

import cz.cvut.zuul.oaas.repos.RefreshTokensRepo;
import cz.cvut.zuul.oaas.models.PersistableRefreshToken;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoRefreshTokensRepo
        extends AbstractMongoRepository<PersistableRefreshToken, String> implements RefreshTokensRepo {


    public void deleteByClientId(String clientId) {
        mongo().remove(query(
                where("authentication.authorization_request.client_id").is(clientId)),
                entityClass());
    }
}
