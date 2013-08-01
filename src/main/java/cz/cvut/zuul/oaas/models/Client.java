package cz.cvut.zuul.oaas.models;

import cz.cvut.zuul.oaas.models.enums.AuthorizationGrant;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@TypeAlias("Client")
@Document(collection = "clients")
public class Client implements ClientDetails {

    private static final long serialVersionUID = 1L;
    private static final String
            EXT_PRODUCT_NAME = "product_name",
            EXT_LOCKED = "locked",
            EXT_IMPLICIT_CLIENT_DETAILS = "implicit_client_details";

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
    private ImplicitClientDetails implicitClientDetails;


    public Client() {
    }
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
    public void setRegisteredRedirectUri(Collection<String> redirectUris) {
        this.registeredRedirectUri = resourceIds != null
            ? new LinkedHashSet<>(redirectUris)
            : Collections.<String>emptySet();
    }

    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities != null
                ? new LinkedHashSet<>(authorities)
                : Collections.<GrantedAuthority>emptySet();
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
            put(EXT_LOCKED, locked);
            put(EXT_IMPLICIT_CLIENT_DETAILS, implicitClientDetails);
        }};
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public ImplicitClientDetails getImplicitClientDetails() {
        return implicitClientDetails;
    }
    public void setImplicitClientDetails(ImplicitClientDetails implicitClientDetails) {
        this.implicitClientDetails = implicitClientDetails;
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
