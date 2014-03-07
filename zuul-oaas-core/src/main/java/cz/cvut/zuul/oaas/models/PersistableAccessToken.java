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
package cz.cvut.zuul.oaas.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

import static java.lang.System.currentTimeMillis;
import static lombok.AccessLevel.NONE;

@Data
@EqualsAndHashCode(of="value")

@CompoundIndexes({
    @CompoundIndex(name="clientId", def="{auth.authzReq.client: 1}"),
    @CompoundIndex(name="username", def="{auth.userAuth.uname: 1}")
})
@TypeAlias("AccessToken")
@Document(collection="access_tokens")

public class PersistableAccessToken implements OAuth2AccessToken, Serializable {

    private static final long serialVersionUID = 3L;

    private static final AuthenticationKeyGenerator AUTH_KEY_GENERATOR = new DefaultAuthenticationKeyGenerator();


    @Id @Setter(NONE)
    private String value;

    @Field("exp")
    @Indexed(expireAfterSeconds=0)
    private Date expiration;

    @Field("type")
    private String tokenType = BEARER_TYPE.toLowerCase();

    @Indexed
    @Field("refToken")
    private String refreshTokenValue;

    @Field("scopes")
    private Set<String> scope;

    @Field("addl")
    private Map<String, Object> additionalInformation = Collections.emptyMap();

    @Indexed
    @Field("authKey") @Setter(NONE)
    private String authenticationKey;

    @Field("auth") @Setter(NONE)
    private OAuth2Authentication authentication;


    public static String extractAuthenticationKey(OAuth2Authentication authentication) {
        return AUTH_KEY_GENERATOR.extractKey(authentication);
    }


    /**
     * Private constructor for persistence and other serialization tools.
     */
    private PersistableAccessToken() {
        this(null);
    }

    public PersistableAccessToken(String value) {
        this.value = value;
    }

    public PersistableAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        this(accessToken.getValue());

        this.additionalInformation = accessToken.getAdditionalInformation();
        this.refreshTokenValue = accessToken.getRefreshToken() != null
                ? accessToken.getRefreshToken().getValue() : null;
        this.expiration = accessToken.getExpiration();
        this.scope = accessToken.getScope();
        this.tokenType = accessToken.getTokenType();

        this.authentication = authentication;
        this.authenticationKey = authentication != null ? extractAuthenticationKey(authentication) : null;
    }


    public int getExpiresIn() {
        return expiration != null
                ? Long.valueOf((expiration.getTime() - currentTimeMillis()) / 1000L).intValue()
                : 0;
    }

    public boolean isExpired() {
        return expiration != null && expiration.before(currentDate());
    }

    public OAuth2RefreshToken getRefreshToken() {
        return refreshTokenValue != null ? new DefaultOAuth2RefreshToken(refreshTokenValue) : null;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        Assert.notNull(authentication);
        this.authentication = authentication;
        this.authenticationKey = extractAuthenticationKey(authentication);
    }

    public String getAuthenticatedClientId() {
        if (authentication != null && authentication.getAuthorizationRequest() != null ) {
            return authentication.getAuthorizationRequest().getClientId();
        }
        return null;
    }

    public String getAuthenticatedUsername() {
        if (authentication != null && authentication.getUserAuthentication() != null) {
            return authentication.getUserAuthentication().getName();
        }
        return null;
    }


    @Override
    public String toString() {
        return value;
    }

    private Date currentDate() {
        return new Date();
    }
}
