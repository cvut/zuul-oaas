package cz.cvut.authserver.oauth2.utils;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public enum AuthorizationGrants {

    clientCredentials("client_credentials"), implict("implicit"), authCode("authorization_code"), 
    resourceOwner("resource_owner"), refreshToken("refresh_token");
    
    private String fullName;

    private AuthorizationGrants(String fullName) {
        this.fullName = fullName;
    }

    public String get() {
        return fullName;
    }
}
