package cz.cvut.authserver.oauth2.models;

import cz.cvut.authserver.oauth2.models.enums.AuthorizationGrant;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.domain.Persistable;
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
public class Client implements ClientDetails, Persistable<String> {

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
	private Collection<GrantedAuthority> authorities = new ArrayList<>(0);
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
    
    public String getId() {
        return clientId;
    }

    public boolean isNew() {
        return true;
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


    public static abstract class fields {
        public static final String
                CLIENT_ID = "client_id",
                CLIENT_SECRET = "client_secret",
                SCOPE = "scope",
                RESOURCE_IDS = "resource_ids",
                AUTHORIZED_GRANT_TYPES = "authorized_grant_types",
                REDIRECT_URI = "redirect_uri",
                AUTHORITIES = "authorities",
                ACCESS_TOKEN_VALIDITY = "access_token_validity",
                REFRESH_TOKEN_VALIDITY = "refresh_token_validity",
                PRODUCT_NAME = "product_name",
                LOCKED = "locked",
                IMPLICIT_CLIENT_DETAILS = "implicit_client_details";
    }
}
