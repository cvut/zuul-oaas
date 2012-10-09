package cz.cvut.authserver.oauth2.models;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class AuthenticatedAccessToken {

    private OAuth2AccessToken accessToken;
    private OAuth2Authentication authentication;


    public AuthenticatedAccessToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        this.accessToken = accessToken;
        this.authentication = authentication;
    }


    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

}
