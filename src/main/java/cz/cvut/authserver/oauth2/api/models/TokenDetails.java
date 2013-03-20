package cz.cvut.authserver.oauth2.api.models;

import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;

/**
 * Represents token details retrieved from the CTU OAuth 2.0 authorization
 * server.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDetails {

    @JsonProperty("token_value")
    private String tokenValue;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("expired")
    private String expired;
    
    @JsonProperty("token_denied")
    private boolean tokenDenied;
    
    @JsonProperty("client_locked")
    private boolean clientLocked;
    
    @JsonProperty("scopes")
    private Set<String> scope;
    
    private ClientDetails clientDetails;
    private UserDetails userDetails;

    public TokenDetails() {
    }

    public TokenDetails(String tokenValue, String tokenType, String expired, boolean tokenDenied, boolean clientLocked, Set<String> scope, ClientDetails clientDetails, UserDetails userDetails) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
        this.expired = expired;
        this.tokenDenied = tokenDenied;
        this.clientLocked = clientLocked;
        this.scope = scope;
        this.clientDetails = clientDetails;
        this.userDetails = userDetails;
    }

    public TokenDetails(String tokenValue, String tokenType, Long expired, boolean tokenDenied, boolean clientLocked, Set<String> scope, ClientDetails clientDetails, Authentication userAuth) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
        this.expired = expired.toString();
        this.tokenDenied = tokenDenied;
        this.clientLocked = clientLocked;
        this.scope = scope;
        this.clientDetails = clientDetails;
        this.userDetails = new User(userAuth.getPrincipal().toString(), "[secured]", userAuth.getAuthorities());
    }
    
    //////////  Getters / Setters  //////////

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public boolean isTokenDenied() {
        return tokenDenied;
    }

    public void setTokenDenied(boolean tokenDenied) {
        this.tokenDenied = tokenDenied;
    }

    public boolean isClientLocked() {
        return clientLocked;
    }

    public void setClientLocked(boolean clientLocked) {
        this.clientLocked = clientLocked;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public ClientDetails getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
    
    
}
