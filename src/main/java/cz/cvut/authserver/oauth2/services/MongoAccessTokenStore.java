package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.models.AuthenticatedAccessToken;
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

    private final MongoOperations mongo;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();


    public MongoAccessTokenStore(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }


    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        mongo.insert(new AuthenticatedAccessToken(accessToken, authentication), ACCESS_TOKENS);
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);

        OAuth2AccessToken accessToken = mongo.findOne(
                query(where(AUTHENTICATION_KEY).is(key)),
                OAuth2AccessToken.class,
                ACCESS_TOKENS);

        if (accessToken == null) {
            LOG.debug("Failed to find access token for authentication {}", authentication);
        }

        if (accessToken != null && !authentication.equals(readAuthentication(accessToken.getValue()))) {
            removeAccessToken(accessToken);
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }
 
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return getAuthenticatedAccessToken(tokenValue).getAccessToken();
    }
     
    public void removeAccessToken(OAuth2AccessToken token) {
        mongo.remove(query(where(TOKEN_ID).is(token.getValue())), ACCESS_TOKENS);
    }
  
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        return getAuthenticatedAccessToken(token).getAuthentication();
    }


    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }


    private AuthenticatedAccessToken getAuthenticatedAccessToken(String tokenId) {
        AuthenticatedAccessToken result = mongo.findById(tokenId, AuthenticatedAccessToken.class, ACCESS_TOKENS);

        if (result == null) {
            LOG.debug("Failed to find access token for token {}", tokenId);
            return new AuthenticatedAccessToken(null, null);
        } else {
            return result;
        }
    }

}
