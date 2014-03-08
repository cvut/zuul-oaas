/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.oauth2;

import cz.cvut.zuul.oaas.repos.AccessTokensRepo;
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo;
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
 * This is a façade for the {@link AccessTokensRepo} and {@link RefreshTokensRepo}
 * that implements {@linkplain TokenStore} interface.
 */
@Setter @Slf4j
public class TokenStoreImpl implements TokenStore {

    private AccessTokensRepo accessTokensRepo;
    private RefreshTokensRepo refreshTokensRepo;



    //////// Delegate to AccessTokens Repository ////////

    public void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        log.debug("Storing access token: [{}]", accessToken);
        accessTokensRepo.save(new PersistableAccessToken(accessToken, authentication));
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        log.debug("Loading access token for client: [{}]", authentication.getAuthorizationRequest() != null
                ? authentication.getAuthorizationRequest().getClientId()
                : "unknown");
        return accessTokensRepo.findOneByAuthentication(authentication);
    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {
        log.debug("Reading access token: [{}]", tokenValue);
        return accessTokensRepo.findOne(tokenValue);
    }

    public void removeAccessToken(OAuth2AccessToken token) {
        log.debug("Removing access token: [{}]", token);
        accessTokensRepo.delete(token.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        log.debug("Removing access token by refresh token: [{}]", refreshToken);
        accessTokensRepo.deleteByRefreshToken(refreshToken);
    }

    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return accessTokensRepo.findByClientId(clientId);
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        return accessTokensRepo.findByUserName(userName);
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    public OAuth2Authentication readAuthentication(String token) {
        log.debug("Reading authentication for access token: [{}]", token);
        PersistableAccessToken result = accessTokensRepo.findOne(token);

        return result != null ? result.getAuthentication() : null;
    }


    //////// Delegate to RefreshTokens Repository ////////

    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        log.debug("Storing refresh token: [{}]", refreshToken);
        refreshTokensRepo.save(new PersistableRefreshToken(refreshToken, authentication));
    }

    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        log.debug("Reading refresh token: [{}]", tokenValue);
        return refreshTokensRepo.findOne(tokenValue);
    }

    public void removeRefreshToken(OAuth2RefreshToken token) {
        log.debug("Removing refresh token: [{}]", token);
        refreshTokensRepo.delete(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        log.debug("Reading authentication for refresh token: [{}]", token.getValue());
        PersistableRefreshToken result = refreshTokensRepo.findOne(token.getValue());

        return result != null ? result.getAuthentication() : null;
    }
}
