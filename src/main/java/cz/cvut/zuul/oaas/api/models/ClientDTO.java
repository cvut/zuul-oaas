package cz.cvut.zuul.oaas.api.models;

import cz.cvut.zuul.oaas.api.validators.EachEnum;
import cz.cvut.zuul.oaas.api.validators.EachURI;
import cz.cvut.zuul.oaas.api.validators.EnumValue;
import cz.cvut.zuul.oaas.api.validators.ValidURI;
import cz.cvut.zuul.oaas.models.ImplicitClientDetails;
import cz.cvut.zuul.oaas.models.enums.AuthorizationGrant;
import cz.jirutka.validator.collection.constraints.EachPattern;
import cz.jirutka.validator.collection.constraints.EachSize;
import cz.jirutka.validator.spring.SpELAssert;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.oauth2.provider.BaseClientDetails.ArrayOrStringDeserializer;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

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
public class ClientDTO implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("client_id")
	private String clientId;

	@JsonProperty("client_secret")
	private String clientSecret;

    @EachSize( @Size(min = 5, max = 255) )
    @EachPattern( @Pattern(regexp = "[\\x21\\x23-\\x5B\\x5D-\\x7E]+",
        message = "{validator.invalid_scope}" )) // see http://tools.ietf.org/html/rfc6749#section-3.3
    @JsonProperty("scope")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> scope;

    //TODO
	@JsonProperty("resource_ids")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> resourceIds;

    @NotEmpty
    @EachEnum( @EnumValue(value = AuthorizationGrant.class,
        message = "{validator.invalid_grant_type}"))
	@JsonProperty("authorized_grant_types")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> authorizedGrantTypes;

    @EachSize( @Size(min = 5, max = 255) )
    @EachURI( @ValidURI(relative = false, fragment = false,
        message = "{validator.invalid_redirect_uri}"))
	@JsonProperty("redirect_uri")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> registeredRedirectUri;

    @JsonProperty("authorities")
    @JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> authorities;

    //TODO
	@JsonProperty("access_token_validity")
	private Integer accessTokenValiditySeconds;

    //TODO
	@JsonProperty("refresh_token_validity")
	private Integer refreshTokenValiditySeconds;

    @JsonProperty("product_name")
	private String productName;

    @JsonProperty("client_locked")
    private Boolean locked;
    
    @JsonProperty("implicit_client_details")
    private ImplicitClientDetails implicitClientDetails;


    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Collection<String> getScope() {
        return scope;
    }
    public void setScope(Collection<String> scope) {
        this.scope = scope;
    }

    public Collection<String> getResourceIds() {
        return resourceIds;
    }
    public void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public Collection<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }
    public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public Collection<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }
    public void setRegisteredRedirectUri(Collection<String> registeredRedirectUri) {
        this.registeredRedirectUri = registeredRedirectUri;
    }

    public Collection<String> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(Collection<String> authorities) {
        this.authorities = authorities;
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

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isLocked() {
        return locked == null ? false : locked;
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


    @SuppressWarnings("UnusedDeclaration")
    public boolean hasRedirectUri() {
        return !registeredRedirectUri.isEmpty();
    }


	@Override
	public String toString() {
        return String.format("Client [%s]", clientId);
	}

}
