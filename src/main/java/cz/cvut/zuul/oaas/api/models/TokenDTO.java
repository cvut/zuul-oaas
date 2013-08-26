package cz.cvut.zuul.oaas.api.models;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class TokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ClientAuthenticationDTO clientAuthentication;

    @JsonProperty("expiration")
    private Date expiration;

    private Set<String> scope;

    private String tokenType;

    private String tokenValue;

    private UserAuthenticationDTO userAuthentication;



    public ClientAuthenticationDTO getClientAuthentication() {
        return clientAuthentication;
    }

    public void setClientAuthentication(ClientAuthenticationDTO clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public UserAuthenticationDTO getUserAuthentication() {
        return userAuthentication;
    }

    public void setUserAuthentication(UserAuthenticationDTO userAuthentication) {
        this.userAuthentication = userAuthentication;
    }
}
