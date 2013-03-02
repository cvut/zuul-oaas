package cz.cvut.authserver.oauth2.models;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class PersistableAccessToken extends DefaultOAuth2AccessToken {

    private static final long serialVersionUID = 1L;
    private static final AuthenticationKeyGenerator authKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private String authenticationKey;
    private OAuth2Authentication authentication;


    public static String extractAuthenticationKey(OAuth2Authentication authentication) {
        return authKeyGenerator.extractKey(authentication);
    }


    public PersistableAccessToken(String value) {
        super(value);
    }

    public PersistableAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        super(accessToken);
        this.authentication = authentication;
        this.authenticationKey = extractAuthenticationKey(authentication);
    }
    

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = authentication;
        this.authenticationKey = extractAuthenticationKey(authentication);
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }
}
