package cz.cvut.authserver.oauth2.models;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.*;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@TypeAlias("Client")
@Document(collection = "clients")
public class Client implements ClientDetails {

    private static final long serialVersionUID = 1L;
    private static final String EXT_PRODUCT_NAME = "product-name";

	private @Id String clientId;
	private String clientSecret;
	private Set<String> scope = new LinkedHashSet<>(0);
	private Set<String> resourceIds = new LinkedHashSet<>(0);
	private Set<String> authorizedGrantTypes = new LinkedHashSet<>(0);
	private Set<String> registeredRedirectUri = new LinkedHashSet<>(0);
	private Collection<GrantedAuthority> authorities = new ArrayList<>(0);
	private Integer accessTokenValiditySeconds;
	private Integer refreshTokenValiditySeconds;

    private String productName;


    public Client() {
    }
    public Client(ClientDetails prototype) {
        this.clientId = prototype.getClientId();
        this.clientSecret = prototype.getClientSecret();
        this.scope = prototype.getScope();
        this.resourceIds = prototype.getResourceIds();
        this.authorizedGrantTypes = prototype.getAuthorizedGrantTypes();
        this.registeredRedirectUri = prototype.getRegisteredRedirectUri();
        this.authorities = prototype.getAuthorities();
        this.accessTokenValiditySeconds = prototype.getAccessTokenValiditySeconds();
        this.refreshTokenValiditySeconds = prototype.getRefreshTokenValiditySeconds();
    }


    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }
    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isScoped() {
        return !CollectionUtils.isEmpty(scope);
    }
    public Set<String> getScope() {
        return scope;
    }
    public void setScope(Collection<String> scope) {
        this.scope = scope != null
                ? new LinkedHashSet<>(scope)
                : Collections.<String>emptySet();
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }
    public void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = resourceIds != null
                ? new LinkedHashSet<>(resourceIds)
                : Collections.<String>emptySet();
    }

    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }
    public void setAuthorizedGrantTypes(Collection<AuthorizationGrant> authorizedGrantTypes) {
        this.authorizedGrantTypes = new LinkedHashSet<>();
        for (AuthorizationGrant grant : authorizedGrantTypes) {
            this.authorizedGrantTypes.add(grant.toString());
        }
    }

    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }
    public void setRegisteredRedirectUri(Collection<URI> registeredRedirectUris) {
        this.registeredRedirectUri = new LinkedHashSet<>();
        for (URI uri : registeredRedirectUris) {
            this.registeredRedirectUri.add(uri.toString());
        }
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities != null
                ? new ArrayList<>(authorities)
                : new ArrayList<GrantedAuthority>(0);
    }

    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }
    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }
    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public Map<String, Object> getAdditionalInformation() {
        return new HashMap<String, Object>() {{
            put(EXT_PRODUCT_NAME, productName);
        }};
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Client other = (Client) obj;
        if (!clientId.equals(other.clientId)) return false;

        return true;
    }

	@Override
	public int hashCode() {
        return new HashCodeBuilder(33, 1).append(clientId).toHashCode();
	}

	@Override
	public String toString() {
        return String.format("Client [%s]", clientId);
	}

}
