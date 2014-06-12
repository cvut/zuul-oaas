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
package cz.cvut.zuul.oaas.api.models;

import cz.cvut.zuul.oaas.api.validators.EachValidURI;
import cz.jirutka.validator.collection.constraints.EachPattern;
import cz.jirutka.validator.collection.constraints.EachSize;
import cz.jirutka.validator.spring.SpELAssert;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.oauth2.provider.BaseClientDetails.ArrayOrStringDeserializer;

import java.io.Serializable;
import java.util.Collection;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

/**
 * DTO for {@link org.springframework.security.oauth2.provider.ClientDetails}.
 */
@Data
@SpELAssert(value="hasRedirectUri()", applyIf="authorizedGrantTypes.contains('authorization_code')",
            message="{validator.missing_redirect_uri}")
public class ClientDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientId;

    private String clientSecret;

    @EachSize(min=5, max=255)
    // see http://tools.ietf.org/html/rfc6749#section-3.3
    @EachPattern(regexp="[\\x21\\x23-\\x5B\\x5D-\\x7E]+", message="{validator.invalid_scope}")
    @JsonDeserialize(using=ArrayOrStringDeserializer.class)
    private Collection<String> scope;

    //TODO
    @JsonDeserialize(using=ArrayOrStringDeserializer.class)
    private Collection<String> resourceIds;

    @NotEmpty
    // see http://tools.ietf.org/html/rfc6749#section-1.3
    @EachPattern(regexp="(client_credentials|implicit|authorization_code|resource_owner|refresh_token)",
                 flags=CASE_INSENSITIVE, message="{validator.invalid_grant_type}")
    @JsonDeserialize(using=ArrayOrStringDeserializer.class)
    private Collection<String> authorizedGrantTypes;

    @EachSize(min=5, max=255)
    @EachValidURI(relative=false, fragment=false, message="{validator.invalid_redirect_uri}")
    @JsonProperty("redirect_uri")
    @JsonDeserialize(using=ArrayOrStringDeserializer.class)
    private Collection<String> registeredRedirectUri;

    @JsonDeserialize(using=ArrayOrStringDeserializer.class)
    private Collection<String> authorities;

    //TODO
    @JsonProperty("access_token_validity")
    private Integer accessTokenValiditySeconds;

    //TODO
    @JsonProperty("refresh_token_validity")
    private Integer refreshTokenValiditySeconds;

    private String productName;

    @JsonProperty("client_locked")
    private boolean locked;

    private String clientType;


    @SuppressWarnings("UnusedDeclaration")
    public boolean hasRedirectUri() {
        return registeredRedirectUri != null && !registeredRedirectUri.isEmpty();
    }
}
