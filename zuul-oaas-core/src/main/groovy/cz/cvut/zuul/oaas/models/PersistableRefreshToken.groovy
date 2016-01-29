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

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.domain.Persistable
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.common.DateUtils.END_OF_TIME

@TypeAlias('RefreshToken')
@Document(collection = "refresh_tokens")
class PersistableRefreshToken
        implements Timestamped, Authenticated, ExpiringOAuth2RefreshToken, Persistable<String> {

    private static final long serialVersionUID = 4L

    @Id
    final String value

    @Field('exp')
    @Indexed(expireAfterSeconds = 0)
    final Date expiration

    @Field('auth')
    final OAuth2Authentication authentication


    @JsonCreator
    PersistableRefreshToken(String value) {
        this(value, END_OF_TIME, null)
    }

    @PersistenceConstructor
    PersistableRefreshToken(String value, Date expiration, OAuth2Authentication authentication) {
        this.value = value
        this.expiration = expiration ?: END_OF_TIME
        this.authentication = authentication
    }

    PersistableRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        this.value = refreshToken.value
        this.authentication = authentication

        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            this.expiration = refreshToken.expiration
        } else {
            this.expiration = END_OF_TIME
        }
    }


    boolean isExpiring() {
        expiration != END_OF_TIME
    }

    @JsonValue
    String getId() { value }

    String toString() { value }

    int hashCode() { value.hashCode() }

    boolean equals(Object that) { toString() == that.toString() }
}
