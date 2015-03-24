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
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import spock.lang.Specification

@Mixin(CoreObjectFactory)
class TokenStoreImplTest extends Specification {

    def accessTokensRepo = Mock(AccessTokensRepo)
    def refreshTokensRepo = Mock(RefreshTokensRepo)

    def accessToken = build(OAuth2AccessToken)
    def refreshToken = build(OAuth2RefreshToken)
    def oauthAuth = build(OAuth2Authentication)
    def persAccessToken = new PersistableAccessToken(accessToken, oauthAuth)
    def persRefreshToken = new PersistableRefreshToken(refreshToken, oauthAuth)

    def store = new TokenStoreImpl(accessTokensRepo, refreshTokensRepo)


    //////// Delegate to AccessTokens Repository ////////

    def "storeAccessToken: creates PersistableAccessToken and saves it to the repo"() {
        when:
            store.storeAccessToken(accessToken, oauthAuth)
        then:
            1 * accessTokensRepo.save({ PersistableAccessToken it ->
                it == accessToken && it.authentication == oauthAuth
            })
    }

    def "getAccessToken: finds access token in the repo by the authentication and returns it"() {
        when:
            def result = store.getAccessToken(oauthAuth)
        then:
            1 * accessTokensRepo.findOneByAuthentication(oauthAuth) >> persAccessToken
            result == persAccessToken
    }

    def "readAccessToken: finds access token in the repo by the values and returns it"() {
        when:
            def result = store.readAccessToken('123')
        then:
            1 * accessTokensRepo.findOne('123') >> persAccessToken
            result == persAccessToken
    }

    def "removeAccessToken: deletes the access token from the repo"() {
        when:
            store.removeAccessToken(accessToken)
        then:
            1 * accessTokensRepo.deleteById(accessToken.value)
    }

    def "removeAccessTokenUsingRefreshToken: deletes access token from the repo using the refresh token"() {
        when:
            store.removeAccessTokenUsingRefreshToken(refreshToken)
        then:
            1 * accessTokensRepo.deleteByRefreshToken(refreshToken)
    }

    def "findTokensByClientId: finds access token in the repo by the clientId and returns them"() {
        given:
            def expected = [ build(PersistableAccessToken), build(PersistableAccessToken) ]
        when:
            def result = store.findTokensByClientId('foo')
        then:
            1 * accessTokensRepo.findByClientId('foo') >> expected
            result == expected
    }

    def "findTokensByClientIdAndUserName: finds access tokens in the repo by the clientId and username and returns them"() {
        given:
            def expected = [ build(PersistableAccessToken), build(PersistableAccessToken) ]
        when:
            def result = store.findTokensByClientIdAndUserName('foo', 'flynn')
        then:
            1 * accessTokensRepo.findByClientIdAndUserName('foo', 'flynn') >> expected
            result == expected
    }

    def "readAuthentication: finds authentication of the access token and returns it"() {
        when:
            def result = store.readAuthentication(accessToken)
        then:
            1 * accessTokensRepo.findOne(accessToken.value) >> persAccessToken
            result == persAccessToken.authentication
    }


    //////// Delegate to RefreshTokens Repository ////////

    def "storeRefreshToken: creates PersistableRefreshToken and saves it in the repo"() {
        when:
            store.storeRefreshToken(refreshToken, oauthAuth)
        then:
            1 * refreshTokensRepo.save({ PersistableRefreshToken it ->
                it == refreshToken && it.authentication == oauthAuth
            })
    }

    def "readRefreshToken: finds refresh token in the repo by the value and returns it"() {
        when:
            def result = store.readRefreshToken('123')
        then:
            1 * refreshTokensRepo.findOne('123') >> persRefreshToken
            result == persRefreshToken
    }

    def "removeRefreshToken: deletes the refresh token from the repo"() {
        when:
            store.removeRefreshToken(refreshToken)
        then:
            1 * refreshTokensRepo.deleteById(refreshToken.value)
    }

    def "readAuthenticationForRefreshToken: finds authentication of the refresh token and returns it"() {
        when:
            def result = store.readAuthenticationForRefreshToken(refreshToken)
        then:
            1 * refreshTokensRepo.findOne(refreshToken.value) >> persRefreshToken
            result == persRefreshToken.authentication
    }
}
