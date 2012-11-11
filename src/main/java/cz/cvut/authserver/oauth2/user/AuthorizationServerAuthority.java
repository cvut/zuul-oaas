package cz.cvut.authserver.oauth2.user;

import javax.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public enum AuthorizationServerAuthority implements GrantedAuthority {

    ADMIN("admin"), RESOURCE("resource"), USER("user"), NONE("none");
    
    private String userType;

    AuthorizationServerAuthority(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    /**
     * The authority granted by this value (same as user type).
     *
     * @return the name of the value
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority() {
        return userType;
    }
}
