package cz.cvut.authserver.oauth2.api.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Date;
import java.util.Set;

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
    private Date expiration;
    
    @JsonProperty("token_denied")
    private boolean tokenDenied;
    
    @JsonProperty("client_locked")
    private boolean clientLocked;
    
    @JsonProperty("scopes")
    private Set<String> scope;
    
    @JsonProperty("client_details")
    private ClientDTO client;
    
    @JsonProperty("user_details")
    private UserDetails userDetails;



    public TokenDetails() {
    }

    public TokenDetails(OAuth2AccessToken token, boolean tokenDenied, ClientDTO client, Authentication userAuth) {
        this.tokenValue = token.getValue();
        this.tokenType = token.getTokenType();
        this.expiration = token.getExpiration();
        this.tokenDenied = tokenDenied;
        this.clientLocked = client.isLocked();
        this.scope = token.getScope();
        this.client = client;
        this.userDetails = (userAuth == null ? 
                null : new User(userAuth.getPrincipal().toString(), 
                                "[secured]", 
                                userAuth.getAuthorities()));
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

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
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

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
    
    
}
