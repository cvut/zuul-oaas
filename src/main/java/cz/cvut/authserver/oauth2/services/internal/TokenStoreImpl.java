package cz.cvut.authserver.oauth2.services.internal;

import cz.cvut.authserver.oauth2.dao.AccessTokenDAO;
import cz.cvut.authserver.oauth2.dao.RefreshTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TokenStoreImpl implements TokenStore {

    private static final Logger LOG = LoggerFactory.getLogger(TokenStoreImpl.class);

    private AccessTokenDAO accessTokenDAO;
    private RefreshTokenDAO refreshTokenDAO;


    
    //////// Delegate to AccessToken DAO ////////

    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        LOG.debug("Storing access token: {}", accessToken);
        accessTokenDAO.save(new PersistableAccessToken(accessToken, authentication));
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return accessTokenDAO.findOneByAuthentication(authentication);
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return accessTokenDAO.findOne(tokenValue);
    }
    
    public void removeAccessToken(OAuth2AccessToken token) {
        LOG.debug("Removing access token: {}", token);
        accessTokenDAO.delete(token.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        LOG.debug("Removing access token by refresh token: {}", refreshToken);
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
        PersistableAccessToken result = accessTokenDAO.findOne(token);

        return result != null ? result.getAuthentication() : null;
    }


    //////// Delegate to RefreshToken DAO ////////

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        LOG.debug("Storing refresh token: {}", refreshToken);
        refreshTokenDAO.save(new PersistableRefreshToken(refreshToken, authentication));
    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return refreshTokenDAO.findOne(tokenValue);
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        LOG.debug("Removing refresh token: {}", token);
        refreshTokenDAO.delete(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        PersistableRefreshToken result = refreshTokenDAO.findOne(token.getValue());

        return result != null ? result.getAuthentication() : null;
    }


    ////////  Accessors  ////////

    public void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.accessTokenDAO = accessTokenDAO;
    }

    public void setRefreshTokenDAO(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO;
    }
}
