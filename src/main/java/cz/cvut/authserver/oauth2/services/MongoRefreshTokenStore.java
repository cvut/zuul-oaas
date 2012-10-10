package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.refresh_tokens.*;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.REFRESH_TOKENS;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoRefreshTokenStore {

    private static final Logger LOG = LoggerFactory.getLogger(MongoRefreshTokenStore.class);
    private static final Class<PersistableRefreshToken> ENTITY_CLASS = PersistableRefreshToken.class;

    private final MongoOperations mongo;


    public MongoRefreshTokenStore(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }


    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        mongo.insert(new PersistableRefreshToken(refreshToken, authentication), REFRESH_TOKENS);
    }

    public OAuth2RefreshToken readRefreshToken(String tokenCode) {
        PersistableRefreshToken token;

        Query query = query(where(TOKEN_ID).is(tokenCode));
        query.fields().exclude(AUTHENTICATION);  // don't load authentication when we're not gonna use it
        token = mongo.findOne(query, ENTITY_CLASS, REFRESH_TOKENS);

        if (token == null) {
            LOG.debug("Failed to find refresh token for token {}", tokenCode);
        }
        return token;
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        mongo.remove(query(where(TOKEN_ID).is(token.getValue())), REFRESH_TOKENS);
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String tokenCode) {
        PersistableRefreshToken token = mongo.findById(tokenCode, ENTITY_CLASS, REFRESH_TOKENS);

        if (token == null) {
            LOG.debug("Failed to find authentication for token {}", tokenCode);
            return null;
        } else {
            return token.getAuthentication();
        }
    }

}
