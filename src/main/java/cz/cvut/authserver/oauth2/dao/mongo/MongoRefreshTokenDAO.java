package cz.cvut.authserver.oauth2.dao.mongo;

import cz.cvut.authserver.oauth2.dao.RefreshTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.REFRESH_TOKENS;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.refresh_tokens.TOKEN_ID;
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

        Query query = query(where(TOKEN_ID).is(tokenCode));
        //query.fields().exclude(AUTHENTICATION);  // don't load authentication when we're not gonna use it
        token = mongo.findOne(query, ENTITY_CLASS, REFRESH_TOKENS);

        return token;
    }

    public void save(PersistableRefreshToken persistableToken) {
        mongo.insert(persistableToken, REFRESH_TOKENS);
    }

    public void delete(OAuth2RefreshToken token) {
        mongo.remove(query(where(TOKEN_ID).is(token.getValue())), REFRESH_TOKENS);
    }

}
