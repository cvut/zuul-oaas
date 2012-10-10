package cz.cvut.authserver.oauth2.models;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class PersistableAccessToken extends DefaultOAuth2AccessToken {

    private String authenticationKey;
    private OAuth2Authentication authentication;


    public PersistableAccessToken(String value) {
        super(value);
    }

    public PersistableAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication, String authenticationKey) {
        super(accessToken);
        this.authentication = authentication;
        this.authenticationKey = authenticationKey;
    }
    

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = authentication;
    }
}
