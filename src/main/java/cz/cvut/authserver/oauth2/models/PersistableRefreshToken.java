package cz.cvut.authserver.oauth2.models;

import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class PersistableRefreshToken implements ExpiringOAuth2RefreshToken {

    private final String value;
    private final Date expiration;
    private OAuth2Authentication authentication;

    
    @JsonCreator
    public PersistableRefreshToken(String value) {
        this.value = value;
        this.expiration = null;
    }

    public PersistableRefreshToken(String value, Date expiration) {
        this.value = value;
        this.expiration = expiration;
    }
    
    public PersistableRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        this.value = refreshToken.getValue();
        this.authentication = authentication;
        
        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            this.expiration = ((ExpiringOAuth2RefreshToken) refreshToken).getExpiration();
        } else {
            this.expiration = null;
        }
    }


    @JsonValue
    public String getValue() {
        return value;
    }

    public Date getExpiration() {
        return expiration;
    }
    
    public boolean isExpiring() {
        return expiration != null;
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = authentication;
    }

    
    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PersistableRefreshToken other = (PersistableRefreshToken) obj;
        return new EqualsBuilder().append(this.value, other.value).isEquals();
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
