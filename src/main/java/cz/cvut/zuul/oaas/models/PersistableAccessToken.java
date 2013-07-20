package cz.cvut.zuul.oaas.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.util.Assert;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@TypeAlias("AccessToken")
@Document(collection = "access_tokens")
public class PersistableAccessToken extends DefaultOAuth2AccessToken {

    private static final long serialVersionUID = 1L;
    private static final AuthenticationKeyGenerator AUTH_KEY_GENERATOR = new DefaultAuthenticationKeyGenerator();

    private String authenticationKey;
    private OAuth2Authentication authentication;


    public static String extractAuthenticationKey(OAuth2Authentication authentication) {
        return AUTH_KEY_GENERATOR.extractKey(authentication);
    }


    private PersistableAccessToken() {
        super((String)null);
    }

    public PersistableAccessToken(String value) {
        super(value);
    }

    public PersistableAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        super(accessToken);
        this.authentication = authentication;
        this.authenticationKey = authentication != null ? extractAuthenticationKey(authentication) : null;
    }


    @Id @Override
    public String getValue() {
        return super.getValue();
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        Assert.notNull(authentication);
        this.authentication = authentication;
        this.authenticationKey = extractAuthenticationKey(authentication);
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public String getAuthenticatedClientId() {
        if (authentication != null && authentication.getAuthorizationRequest() != null ) {
            return authentication.getAuthorizationRequest().getClientId();
        }
        return null;
    }

    public String getAuthenticatedUsername() {
        if (authentication != null && authentication.getUserAuthentication() != null) {
            return authentication.getUserAuthentication().getName();
        }
        return null;
    }

    public String getRefreshTokenValue() {
        return getRefreshToken() != null ? getRefreshToken().getValue() : null;
    }
}
