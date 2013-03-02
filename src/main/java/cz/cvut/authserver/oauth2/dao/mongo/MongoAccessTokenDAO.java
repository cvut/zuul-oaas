package cz.cvut.authserver.oauth2.dao.mongo;

import cz.cvut.authserver.oauth2.dao.AccessTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collection;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.access_tokens.*;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.ACCESS_TOKENS;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoAccessTokenDAO implements AccessTokenDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MongoAccessTokenDAO.class);
    private static final Class<PersistableAccessToken> ENTITY_CLASS = PersistableAccessToken.class;

    private static final IndexDefinition[] INDEXES = {
        new Index().on(AUTHENTICATION_KEY, Order.ASCENDING),
        new Index().on(REFRESH_TOKEN, Order.ASCENDING),
        new Index().on(CLIENT_ID, Order.ASCENDING),
        new Index().on(USER_NAME, Order.ASCENDING)
    };

    private final MongoOperations mongo;


    public MongoAccessTokenDAO(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
        ensureIndexes();
    }


    public PersistableAccessToken findOne(String tokenCode) throws EmptyResultDataAccessException {
        Query query = query(where(TOKEN_ID).is(tokenCode));
        //query.fields().exclude(AUTHENTICATION);  // don't load authentication when we're not gonna use it

        return mongo.findOne(query, ENTITY_CLASS, ACCESS_TOKENS);
    }

    public PersistableAccessToken findByAuthentication(OAuth2Authentication authentication) {
        String authKey = PersistableAccessToken.extractAuthenticationKey(authentication);

        PersistableAccessToken accessToken = mongo.findOne(
                query(where(AUTHENTICATION_KEY).is(authKey)),
                ENTITY_CLASS, ACCESS_TOKENS);

        if (accessToken == null) {
            LOG.debug("Failed to find access token for authentication {}", authentication);
        }

        if (accessToken != null && !authentication.equals(accessToken.getAuthentication())) {
            remove(accessToken);
            // keep the store consistent (maybe the same user is represented by this auth. but the details have changed)
            save(new PersistableAccessToken(accessToken, authentication));
        }
        return accessToken;
    }

    public Collection<OAuth2AccessToken> findByClientId(String clientId) {
        return findTokensBy(CLIENT_ID, clientId);
    }

    public Collection<OAuth2AccessToken> findByUserName(String userName) {
        return findTokensBy(USER_NAME, userName);
    }

    public void save(PersistableAccessToken persistableAccessToken) {
        mongo.insert(persistableAccessToken, ACCESS_TOKENS);
    }

    public void remove(OAuth2AccessToken token) {
        mongo.remove(query(where(TOKEN_ID).is(token.getValue())), ACCESS_TOKENS);
    }

    public void removeByRefreshToken(OAuth2RefreshToken refreshToken) {
        mongo.remove(query(where(REFRESH_TOKEN).is(refreshToken.getValue())), ACCESS_TOKENS);
    }


    private void ensureIndexes() {
        for (IndexDefinition index : INDEXES) {
            mongo.indexOps(ACCESS_TOKENS).ensureIndex(index);
        }
    }

    private Collection<OAuth2AccessToken> findTokensBy(String field, Object value) {
        Query query = query(where(field).is(value));
        query.fields().exclude(AUTHENTICATION);

        return (Collection) mongo.find(query, ENTITY_CLASS, ACCESS_TOKENS);
    }
}
