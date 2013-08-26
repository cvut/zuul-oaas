package cz.cvut.zuul.oaas.services.internal;

import cz.cvut.zuul.oaas.dao.AccessTokenDAO;
import cz.cvut.zuul.oaas.dao.RefreshTokenDAO;
import cz.cvut.zuul.oaas.models.PersistableAccessToken;
import cz.cvut.zuul.oaas.models.PersistableRefreshToken;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Collection;


/**
 * This is a façade for the {@link AccessTokenDAO} and {@link RefreshTokenDAO}
 * that implements {@linkplain TokenStore} interface.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Setter @Slf4j
public class TokenStoreImpl implements TokenStore {

    private AccessTokenDAO accessTokenDAO;
    private RefreshTokenDAO refreshTokenDAO;


    
    //////// Delegate to AccessToken DAO ////////

    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        log.debug("Storing access token: [{}]", accessToken);
        accessTokenDAO.save(new PersistableAccessToken(accessToken, authentication));
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        log.debug("Loading access token for client: [{}]", authentication.getAuthorizationRequest() != null
                ? authentication.getAuthorizationRequest().getClientId()
                : "unknown");
        return accessTokenDAO.findOneByAuthentication(authentication);
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        log.debug("Reading access token: [{}]", tokenValue);
        return accessTokenDAO.findOne(tokenValue);
    }
    
    public void removeAccessToken(OAuth2AccessToken token) {
        log.debug("Removing access token: [{}]", token);
        accessTokenDAO.delete(token.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        log.debug("Removing access token by refresh token: [{}]", refreshToken);
        accessTokenDAO.deleteByRefreshToken(refreshToken);
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return accessTokenDAO.findByClientId(clientId);
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        return accessTokenDAO.findByUserName(userName);
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        log.debug("Reading authentication for access token: [{}]", token);
        PersistableAccessToken result = accessTokenDAO.findOne(token);

        return result != null ? result.getAuthentication() : null;
    }


    //////// Delegate to RefreshToken DAO ////////

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        log.debug("Storing refresh token: [{}]", refreshToken);
        refreshTokenDAO.save(new PersistableRefreshToken(refreshToken, authentication));
    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        log.debug("Reading refresh token: [{}]", tokenValue);
        return refreshTokenDAO.findOne(tokenValue);
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        log.debug("Removing refresh token: [{}]", token);
        refreshTokenDAO.delete(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        log.debug("Reading authentication for refresh token: [{}]", token.getValue());
        PersistableRefreshToken result = refreshTokenDAO.findOne(token.getValue());

        return result != null ? result.getAuthentication() : null;
    }
}
