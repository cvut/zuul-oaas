package cz.cvut.zuul.oaas.dao.mongo;

import cz.cvut.zuul.oaas.dao.AccessTokenDAO;
import cz.cvut.zuul.oaas.models.PersistableAccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.annotation.PostConstruct;
import java.util.Collection;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoAccessTokenDAO
        extends AbstractMongoGenericDAO<PersistableAccessToken, String> implements AccessTokenDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MongoAccessTokenDAO.class);
    private static final String
            CLIENT_ID = "authentication.authorization_request.client_id",
            USER_NAME = "authentication.user_authentication.user_name";

    private static final IndexDefinition[] INDEXES = {
        new Index().on("authenticationKey", Order.ASCENDING),
        new Index().on("refreshToken", Order.ASCENDING),
        new Index().on(CLIENT_ID, Order.ASCENDING),
        new Index().on(USER_NAME, Order.ASCENDING)
    };


    @PostConstruct
    protected void ensureIndexes() {
        for (IndexDefinition index : INDEXES) {
            mongo().indexOps(entityClass()).ensureIndex(index);
        }
    }


    public PersistableAccessToken findOneByAuthentication(OAuth2Authentication authentication) {
        String authKey = PersistableAccessToken.extractAuthenticationKey(authentication);

        PersistableAccessToken accessToken = mongo().findOne(query(
                where("authenticationKey").is(authKey)),
                entityClass());

        if (accessToken == null) {
            LOG.debug("Failed to find access token for authentication: [{}] with key: [{}]", authentication, authKey);
        }

        if (accessToken != null && !authentication.equals(accessToken.getAuthentication())) {
            LOG.debug("Stored authentication details differs from given one, updating to keep the store consistent");
            delete(accessToken); //TODO not needed?
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

    public void deleteByRefreshToken(OAuth2RefreshToken refreshToken) {
        mongo().remove(query(
                where("refreshToken").is(refreshToken.getValue())),
                entityClass());
    }

    public void deleteByClientId(String clientId) {
        mongo().remove(query(
                where(CLIENT_ID).is(clientId)),
                entityClass());
    }


    private Collection<OAuth2AccessToken> findTokensBy(String field, Object value) {
        Query query = query(where(field).is(value));
        query.fields().exclude("authentication");

        return (Collection) mongo().find(query, entityClass());
    }
}
