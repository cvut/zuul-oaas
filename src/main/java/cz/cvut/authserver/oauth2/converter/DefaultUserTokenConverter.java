package cz.cvut.authserver.oauth2.converter;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class DefaultUserTokenConverter implements UserTokenConverter {

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        if (authentication.getPrincipal() instanceof User) {
            User principal = (User) authentication.getPrincipal();
            response.put("user-login", principal.getUsername());
            response.put("user-authorities", principal.getAuthorities());
            response.put("user-locked", !principal.isAccountNonLocked());
        }
        return response;
    }
}
