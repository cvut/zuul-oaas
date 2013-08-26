package cz.cvut.zuul.oaas.models;

import cz.cvut.zuul.oaas.models.enums.AuthorizationGrant;
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

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
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
            put(EXT_IMPLICIT_CLIENT_DETAILS, implicitClientDetails);
        }};
    }
}
