package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.access_tokens.*;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.ACCESS_TOKENS;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoAccessTokenStore {

    private static final Logger LOG = LoggerFactory.getLogger(MongoAccessTokenStore.class);
    private static final Class<PersistableAccessToken> ENTITY_CLASS = PersistableAccessToken.class;

    private final MongoOperations mongo;
    private AuthenticationKeyGenerator authKeyGenerator = new DefaultAuthenticationKeyGenerator();


    public MongoAccessTokenStore(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }


    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String authKey = authKeyGenerator.extractKey(authentication);

        mongo.insert(new PersistableAccessToken(accessToken, authentication, authKey), ACCESS_TOKENS);
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String authKey = authKeyGenerator.extractKey(authentication);

        PersistableAccessToken accessToken = mongo.findOne(
                query(where(AUTHENTICATION_KEY).is(authKey)),
                ENTITY_CLASS, ACCESS_TOKENS);

        if (accessToken == null) {
            LOG.debug("Failed to find access token for authentication {}", authentication);
        }

        if (accessToken != null && !authentication.equals(accessToken.getAuthentication())) {
            removeAccessToken(accessToken);
            // keep the store consistent (maybe the same user is represented by this auth. but the details have changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }
 
    public OAuth2AccessToken readAccessToken(String tokenCode) {
        OAuth2AccessToken token = mongo.findById(tokenCode, ENTITY_CLASS, ACCESS_TOKENS);

        if (token == null) {
            LOG.debug("Failed to find access token for token {}", tokenCode);
        }
        return token;
    }
     
    public void removeAccessToken(OAuth2AccessToken token) {
        mongo.remove(query(where(TOKEN_ID).is(token.getValue())), ACCESS_TOKENS);
    }
  
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        mongo.remove(query(where(REFRESH_TOKEN).is(refreshToken.getValue())), ACCESS_TOKENS);
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return findTokensBy(CLIENT_ID, clientId);
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        return findTokensBy(USER_NAME, userName);
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String tokenCode) {
        PersistableAccessToken token = mongo.findById(tokenCode, ENTITY_CLASS, ACCESS_TOKENS);

        if (token == null) {
            LOG.debug("Failed to find authentication for token {}", tokenCode);
            return null;
        } else {
            return token.getAuthentication();
        }
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authKeyGenerator = authenticationKeyGenerator;
    }


    private Collection<OAuth2AccessToken> findTokensBy(String field, Object value) {
        return (Collection) mongo.find(query(where(field).is(value)), PersistableAccessToken.class, ACCESS_TOKENS);
    }
}
