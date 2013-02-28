package cz.cvut.authserver.oauth2.api.models;

import cz.cvut.authserver.oauth2.api.validators.constraint.EachEnum;
import cz.cvut.authserver.oauth2.api.validators.constraint.EachURI;
import cz.cvut.authserver.oauth2.api.validators.constraint.EnumValue;
import cz.cvut.authserver.oauth2.api.validators.constraint.ValidURI;
import cz.cvut.authserver.oauth2.models.AuthorizationGrant;
import cz.jirutka.validator.collection.constraints.EachPattern;
import cz.jirutka.validator.collection.constraints.EachSize;
import cz.jirutka.validator.spring.SpELAssert;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.BaseClientDetails.ArrayOrStringDeserializer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * DTO for {@link ClientDetails}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@SpELAssert(value = "hasRedirectUri()", applyIf = "authorizedGrantTypes.contains('auth_code')",
            message = "{validator.missing_redirect_uri}")
@JsonAutoDetect(JsonMethod.NONE)
@JsonSerialize(include = Inclusion.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDTO implements ClientDetails {

    private static final String EXT_PRODUCT_NAME = "product-name";

	@JsonProperty("client_id")
	private String clientId;

	@JsonProperty("client_secret")
	private String clientSecret;

    @NotEmpty
    @EachSize( @Size(min = 5, max = 255) )
    @EachPattern( @Pattern(regexp = "[a-zA-Z0-9\\-_\\.]+") )
    @JsonProperty("scope")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Set<String> scope = new LinkedHashSet<>(0);

    //TODO
	@JsonProperty("resource_ids")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Set<String> resourceIds = new LinkedHashSet<>(0);

    @NotEmpty
    @EachEnum( @EnumValue(value = AuthorizationGrant.class,
        message = "{validator.invalid_grant_type}"))
	@JsonProperty("authorized_grant_types")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Set<String> authorizedGrantTypes = new LinkedHashSet<>(0);

    @EachSize( @Size(min = 5, max = 255) )
    @EachURI( @ValidURI(relative = false, fragment = false) )
	@JsonProperty("redirect_uri")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Set<String> registeredRedirectUris = new LinkedHashSet<>(0);

	private List<GrantedAuthority> authorities = new ArrayList<>(0);

    //TODO
	@JsonProperty("access_token_validity")
	private Integer accessTokenValiditySeconds;

    //TODO
	@JsonProperty("refresh_token_validity")
	private Integer refreshTokenValiditySeconds;

	private Map<String, Object> additionalInformation = new LinkedHashMap<>();



	public ClientDTO() {
	}

	public ClientDTO(ClientDetails prototype) {
		this();
		setAccessTokenValiditySeconds(prototype.getAccessTokenValiditySeconds());
		setRefreshTokenValiditySeconds(prototype.getRefreshTokenValiditySeconds());
		setAuthorities(prototype.getAuthorities());
		setAuthorizedGrantTypes(prototype.getAuthorizedGrantTypes());
		setClientId(prototype.getClientId());
		setClientSecret(prototype.getClientSecret());
		setRegisteredRedirectUri(prototype.getRegisteredRedirectUri());
		setScope(prototype.getScope());
		setResourceIds(prototype.getResourceIds());
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
		this.scope = scope != null ? new LinkedHashSet<>(scope) : new LinkedHashSet<String>(0);
	}


	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(Collection<String> resourceIds) {
		this.resourceIds = resourceIds != null
                ? new LinkedHashSet<>(resourceIds)
                : new LinkedHashSet<String>(0);
	}


	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes) {
		this.authorizedGrantTypes = new LinkedHashSet<>(authorizedGrantTypes);
	}


	public Set<String> getRegisteredRedirectUri() {
		return registeredRedirectUris;
	}

	public void setRegisteredRedirectUri(Set<String> registeredRedirectUris) {
		this.registeredRedirectUris = registeredRedirectUris != null
                ? new LinkedHashSet<>(registeredRedirectUris)
                : new LinkedHashSet<String>(0);
	}

    @SuppressWarnings("UnusedDeclaration")
    public boolean hasRedirectUri() {
        return !registeredRedirectUris.isEmpty();
    }


    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }


    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = new ArrayList<>(authorities);
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalInformation() {
        return Collections.unmodifiableMap(this.additionalInformation);
    }

	public void setAdditionalInformation(Map<String, ?> additionalInformation) {
		this.additionalInformation = new LinkedHashMap<>(additionalInformation);
	}


	@JsonAnySetter
	public void addAdditionalInformation(String key, Object value) {
		additionalInformation.put(key, value);
	}

    public String getProductName() {
        return (String) additionalInformation.get(EXT_PRODUCT_NAME);
    }


    public void setProductName(String productName) {
        additionalInformation.put(EXT_PRODUCT_NAME, productName);
    }


    @JsonProperty("authorities")
    protected List<String> getAuthoritiesAsStrings() {
        return new ArrayList<>(AuthorityUtils.authorityListToSet(authorities));
    }

    @JsonProperty("authorities")
    @JsonDeserialize(using = ArrayOrStringDeserializer.class)
    protected void setAuthoritiesAsStrings(Set<String> values) {
        setAuthorities(AuthorityUtils.createAuthorityList(values.toArray(new String[values.size()])));
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        ClientDTO other = (ClientDTO) obj;
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
