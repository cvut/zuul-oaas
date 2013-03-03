package cz.cvut.authserver.oauth2.dao.mongo;

import cz.cvut.authserver.oauth2.dao.RefreshTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoRefreshTokenDAO implements RefreshTokenDAO {

    private static final Class<PersistableRefreshToken> ENTITY_CLASS = PersistableRefreshToken.class;

    private final MongoOperations mongo;


    public MongoRefreshTokenDAO(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }


    public PersistableRefreshToken findOne(String tokenCode) {
        PersistableRefreshToken token;

        Query query = query(where("_id").is(tokenCode));
        //query.fields().exclude(AUTHENTICATION);  // don't load authentication when we're not gonna use it
        token = mongo.findOne(query, ENTITY_CLASS);

        return token;
    }

    public void save(PersistableRefreshToken persistableToken) {
        mongo.insert(persistableToken);
    }

    public void delete(OAuth2RefreshToken token) {
        mongo.remove(query(where("_id").is(token.getValue())), ENTITY_CLASS);
    }

}
