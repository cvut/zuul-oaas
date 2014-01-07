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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(of="value")
@TypeAlias("RefreshToken")
@Document(collection = "refresh_tokens")
public class PersistableRefreshToken implements ExpiringOAuth2RefreshToken, Serializable {

    private static final long serialVersionUID = 1L;

    public static final Date NON_EXPIRING_DATE = new Date(Long.MAX_VALUE);

    private @Id String value;
    private Date expiration;
    private @Getter OAuth2Authentication authentication;


    protected PersistableRefreshToken() {
    }

    @JsonCreator
    public PersistableRefreshToken(String value) {
        this.value = value;
        this.expiration = null;
    }

    public PersistableRefreshToken(String value, Date expiration) {
        this.value = value;
        this.expiration = expiration;
    }
    
    public PersistableRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        this.value = refreshToken.getValue();
        this.authentication = authentication;
        
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            this.expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
        }
    }


    @JsonValue
    public String getValue() {
        return value;
    }

    public Date getExpiration() {
        return expiration != null ? expiration : NON_EXPIRING_DATE;
    }
    
    public boolean isExpiring() {
        return expiration != null;
    }


    @Override
    public String toString() {
        return getValue();
    }
}
