package cz.cvut.zuul.oaas.api.models;

import cz.cvut.zuul.oaas.api.validators.EachURI;
import cz.cvut.zuul.oaas.api.validators.ValidURI;
import cz.jirutka.validator.collection.constraints.EachPattern;
import cz.jirutka.validator.collection.constraints.EachSize;
import cz.jirutka.validator.spring.SpELAssert;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.oauth2.provider.BaseClientDetails.ArrayOrStringDeserializer;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

/**
 * DTO for {@link ClientDetails}.
 */
@Data
@SpELAssert(value = "hasRedirectUri()", applyIf = "authorizedGrantTypes.contains('authorization_code')",
            message = "{validator.missing_redirect_uri}")
public class ClientDTO implements Serializable {

    private static final long serialVersionUID = 1L;

	private String clientId;

	private String clientSecret;

    @EachSize( @Size(min = 5, max = 255) )
    @EachPattern( @Pattern(regexp = "[\\x21\\x23-\\x5B\\x5D-\\x7E]+",
        message = "{validator.invalid_scope}" )) // see http://tools.ietf.org/html/rfc6749#section-3.3
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> scope;

    //TODO
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> resourceIds;

    @NotEmpty
    @EachPattern( @Pattern( // see http://tools.ietf.org/html/rfc6749#section-1.3
        regexp = "(client_credentials|implicit|authorization_code|resource_owner|refresh_token)",
        flags = CASE_INSENSITIVE, message = "{validator.invalid_grant_type}"))
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> authorizedGrantTypes;

    @EachSize( @Size(min = 5, max = 255) )
    @EachURI( @ValidURI(relative = false, fragment = false,
        message = "{validator.invalid_redirect_uri}"))
	@JsonProperty("redirect_uri")
	@JsonDeserialize(using = ArrayOrStringDeserializer.class)
	private Collection<String> registeredRedirectUri;

    @JsonDeserialize(using = ArrayOrStringDeserializer.class)
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
        return !registeredRedirectUri.isEmpty();
    }
}
