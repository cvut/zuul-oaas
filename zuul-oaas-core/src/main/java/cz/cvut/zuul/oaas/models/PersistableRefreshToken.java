package cz.cvut.zuul.oaas.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(of="value")
@TypeAlias("RefreshToken")
@Document(collection = "refresh_tokens")
public class PersistableRefreshToken implements ExpiringOAuth2RefreshToken, Serializable {

    private static final long serialVersionUID = 1L;

    public static final Date NON_EXPIRING_DATE = new Date(Long.MAX_VALUE);

    private @Id String value;
    private Date expiration;
    private @Getter OAuth2Authentication authentication;


    protected PersistableRefreshToken() {
    }

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
        }
    }


    @JsonValue
    public String getValue() {
        return value;
    }

    public Date getExpiration() {
        return expiration != null ? expiration : NON_EXPIRING_DATE;
    }
    
    public boolean isExpiring() {
        return expiration != null;
    }


    @Override
    public String toString() {
        return getValue();
    }
}
