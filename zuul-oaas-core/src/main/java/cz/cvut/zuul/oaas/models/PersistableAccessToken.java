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

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.util.Assert;

@TypeAlias("AccessToken")
@Document(collection = "access_tokens")
public class PersistableAccessToken extends DefaultOAuth2AccessToken {

    private static final long serialVersionUID = 1L;
    private static final AuthenticationKeyGenerator AUTH_KEY_GENERATOR = new DefaultAuthenticationKeyGenerator();

    private String authenticationKey;
    private @Getter OAuth2Authentication authentication;


    public static String extractAuthenticationKey(OAuth2Authentication authentication) {
        return AUTH_KEY_GENERATOR.extractKey(authentication);
    }


    private PersistableAccessToken() {
        super((String)null);
    }

    public PersistableAccessToken(String value) {
        super(value);
    }

    public PersistableAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        super(accessToken);
        this.authentication = authentication;
        this.authenticationKey = authentication != null ? extractAuthenticationKey(authentication) : null;
    }


    @Id @Override
    public String getValue() {
        return super.getValue();
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        Assert.notNull(authentication);
        this.authentication = authentication;
        this.authenticationKey = extractAuthenticationKey(authentication);
    }

    public String getAuthenticationKey() {
        return authenticationKey;
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

    public String getRefreshTokenValue() {
        return getRefreshToken() != null ? getRefreshToken().getValue() : null;
    }
}
