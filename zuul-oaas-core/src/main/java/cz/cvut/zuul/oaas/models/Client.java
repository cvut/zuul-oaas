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
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of="clientId")
@ToString(of={"clientId", "productName"})

@TypeAlias("Client")
@Document(collection = "clients")

public class Client implements ClientDetails {

    private static final long serialVersionUID = 1L;
    private static final String
            EXT_PRODUCT_NAME = "product_name",
            EXT_LOCKED = "locked",
            EXT_CLIENT_TYPE = "client_type";

	private @Id String clientId;
	private String clientSecret;
	private Set<String> scope = new LinkedHashSet<>(0);
	private Set<String> resourceIds = new LinkedHashSet<>(0);
	private Set<String> authorizedGrantTypes = new LinkedHashSet<>(0);
	private Set<String> registeredRedirectUri = new LinkedHashSet<>(0);
	private Set<GrantedAuthority> authorities = new LinkedHashSet<>(0);
	private Integer accessTokenValiditySeconds;
	private Integer refreshTokenValiditySeconds;

    private String productName;
    private boolean locked = false;
    private String clientType;


    public Client(ClientDetails prototype) {
        this.clientId = prototype.getClientId();
        this.clientSecret = prototype.getClientSecret();
        this.scope = prototype.getScope();
        this.resourceIds = prototype.getResourceIds();
        this.authorizedGrantTypes = prototype.getAuthorizedGrantTypes();
        this.registeredRedirectUri = prototype.getRegisteredRedirectUri();
        this.authorities = new LinkedHashSet<>(prototype.getAuthorities());
        this.accessTokenValiditySeconds = prototype.getAccessTokenValiditySeconds();
        this.refreshTokenValiditySeconds = prototype.getRefreshTokenValiditySeconds();
    }


    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    public boolean isScoped() {
        return !CollectionUtils.isEmpty(scope);
    }

    public void setScope(Collection<String> scope) {
        this.scope = scope != null
                ? new LinkedHashSet<>(scope)
                : Collections.<String>emptySet();
    }

    public void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = resourceIds != null
                ? new LinkedHashSet<>(resourceIds)
                : Collections.<String>emptySet();
    }

    public void setAuthorizedGrantTypes(Collection<AuthorizationGrant> authorizedGrantTypes) {
        this.authorizedGrantTypes = new LinkedHashSet<>();
        for (AuthorizationGrant grant : authorizedGrantTypes) {
            this.authorizedGrantTypes.add(grant.toString());
        }
    }

    public void setRegisteredRedirectUri(Collection<String> redirectUris) {
        this.registeredRedirectUri = resourceIds != null
            ? new LinkedHashSet<>(redirectUris)
            : Collections.<String>emptySet();
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities != null
                ? new LinkedHashSet<>(authorities)
                : Collections.<GrantedAuthority>emptySet();
    }

    public Map<String, Object> getAdditionalInformation() {
        return new HashMap<String, Object>() {{
            put(EXT_PRODUCT_NAME, productName);
            put(EXT_LOCKED, locked);
            put(EXT_CLIENT_TYPE, clientType);
        }};
    }
}
