package cz.cvut.authserver.oauth2.services;

import java.util.Collection;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;


/**
 * MongoDB implementation of the {@linkplain TokenStore} service.
 *
 * This is actually a façade for the {@link MongoAccessTokenStore} and
 * {@link MongoRefreshTokenStore}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoTokenStore implements TokenStore {

    private final MongoAccessTokenStore accessTokenStore;


    public MongoTokenStore(MongoOperations mongoTemplate) {
        this.accessTokenStore = new MongoAccessTokenStore(mongoTemplate);
    }

    
    //////// Delegate to accessTokenStore ////////

    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        accessTokenStore.storeAccessToken(accessToken, authentication);
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return accessTokenStore.getAccessToken(authentication);
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return accessTokenStore.readAccessToken(tokenValue);
    }
    
    public void removeAccessToken(OAuth2AccessToken token) {
        accessTokenStore.removeAccessToken(token);
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        accessTokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return accessTokenStore.findTokensByClientId(clientId);
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        return accessTokenStore.findTokensByUserName(userName);
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return accessTokenStore.readAuthentication(token);
    }

    public OAuth2Authentication readAuthentication(String token) {
        return accessTokenStore.readAuthentication(token);
    }

    
    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        accessTokenStore.setAuthenticationKeyGenerator(authenticationKeyGenerator);
    }



    //////// Delegate to refreshTokenStore ////////

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
