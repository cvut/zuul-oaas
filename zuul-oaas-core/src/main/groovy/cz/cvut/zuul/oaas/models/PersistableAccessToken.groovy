/*
 * The MIT License
 *
 * Copyright 2013-2016 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.models

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.domain.Persistable
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator
import org.springframework.util.Assert

import static java.lang.System.currentTimeMillis

@CompoundIndexes([
    @CompoundIndex(name = 'clientId', def = '{auth.oauthReq.client: 1}'),
    @CompoundIndex(name = 'username', def = '{auth.userAuth.uname: 1}')
])
@TypeAlias('AccessToken')
@Document(collection = 'access_tokens')
class PersistableAccessToken implements Timestamped, Authenticated, OAuth2AccessToken, Persistable<String> {

    private static final long serialVersionUID = 5L

    private static final AuthenticationKeyGenerator AUTH_KEY_GENERATOR = new DefaultAuthenticationKeyGenerator()


    @Id
    String value

    @Field('exp')
    @Indexed(expireAfterSeconds = 0)
    Date expiration

    @Field('type')
    String tokenType = BEARER_TYPE.toLowerCase()

    @Indexed
    @Field('refToken')
    String refreshTokenValue

    @Field('scopes')
    Set<String> scope

    @Field('addl')
    Map<String, Object> additionalInformation = [:]

    @Indexed
    @Field('authKey')
    private String authenticationKey

    @Field('auth')
    OAuth2Authentication authentication


    PersistableAccessToken() {
    }

    PersistableAccessToken(String value) {
        this.value = value
    }

    PersistableAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        this(accessToken.value)

        this.expiration = accessToken.expiration
        this.tokenType = accessToken.tokenType
        this.refreshTokenValue = accessToken?.refreshToken?.value
        this.scope = accessToken.scope
        this.additionalInformation = accessToken.additionalInformation

        this.authenticationKey = authentication != null ? extractAuthenticationKey(authentication) : null
        this.authentication = authentication
    }


    static String extractAuthenticationKey(OAuth2Authentication authentication) {
        AUTH_KEY_GENERATOR.extractKey(authentication)
    }


    int getExpiresIn() {
        expiration != null ? ((expiration.time - currentTimeMillis()) / 1000L).intValue() : 0
    }

    boolean isExpired() {
        expiration?.before(new Date())
    }

    OAuth2RefreshToken getRefreshToken() {
        refreshTokenValue != null ? new DefaultOAuth2RefreshToken(refreshTokenValue) : null
    }

    void setAuthentication(OAuth2Authentication authentication) {
        Assert.notNull(authentication)
        this.authentication = authentication
        this.authenticationKey = extractAuthenticationKey(authentication)
    }


    String getId() { value }

    String toString() { value }

    int hashCode() { value.hashCode() }

    boolean equals(Object that) { toString() == that.toString() }
}
