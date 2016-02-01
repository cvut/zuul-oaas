/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.oauth2

import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.models.PersistableRefreshToken
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo
import groovy.util.logging.Slf4j
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenStore

/**
 * This class adapts {@link AccessTokensRepo} and {@link RefreshTokensRepo} to
 * the {@link TokenStore} interface from the Spring Security OAuth framework.
 */
@Slf4j
class TokenStoreAdapter implements TokenStore {

    final AccessTokensRepo accessTokensRepo
    final RefreshTokensRepo refreshTokensRepo


    TokenStoreAdapter(AccessTokensRepo accessTokensRepo, RefreshTokensRepo refreshTokensRepo) {
        this.accessTokensRepo = accessTokensRepo
        this.refreshTokensRepo = refreshTokensRepo
    }


    //////// Delegate to AccessTokens Repository ////////

    void storeAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        if (accessToken instanceof PersistableAccessToken) {
            accessToken.authentication = authentication
            log.debug 'Updating access token: [{}]', accessToken
            accessTokensRepo.save(accessToken)

        } else {
            log.debug 'Storing access token: [{}]', accessToken
            accessTokensRepo.save(new PersistableAccessToken(accessToken, authentication))
        }
    }

    OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        log.debug 'Loading access token for client: [{}]',
                authentication.getOAuth2Request()?.clientId ?: 'unknown'

        accessTokensRepo.findOneByAuthentication(authentication)
    }

    OAuth2AccessToken readAccessToken(String tokenValue) {
        log.debug 'Reading access token: [{}]', tokenValue
        accessTokensRepo.findOne(tokenValue)
    }

    void removeAccessToken(OAuth2AccessToken token) {
        log.debug 'Removing access token: [{}]', token
        accessTokensRepo.deleteById(token.value)
    }

    void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        log.debug 'Removing access token by refresh token: [{}]', refreshToken
        accessTokensRepo.deleteByRefreshToken(refreshToken)
    }

    Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        accessTokensRepo.findByClientId(clientId)
    }

    Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        accessTokensRepo.findByClientIdAndUserName(clientId, userName)
    }

    OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        readAuthentication(token.value)
    }

    OAuth2Authentication readAuthentication(String token) {
        log.debug 'Reading authentication for access token: [{}]', token
        accessTokensRepo.findOne(token)?.authentication
    }


    //////// Delegate to RefreshTokens Repository ////////

    void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        log.debug 'Storing refresh token: [{}]', refreshToken
        refreshTokensRepo.save(new PersistableRefreshToken(refreshToken, authentication))
    }

    OAuth2RefreshToken readRefreshToken(String tokenValue) {
        log.debug 'Reading refresh token: [{}]', tokenValue
        refreshTokensRepo.findOne(tokenValue)
    }

    void removeRefreshToken(OAuth2RefreshToken token) {
        log.debug 'Removing refresh token: [{}]', token
        refreshTokensRepo.deleteById(token.value)
    }

    OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        log.debug 'Reading authentication for refresh token: [{}]', token.value
        refreshTokensRepo.findOne(token.value)?.authentication
    }
}
