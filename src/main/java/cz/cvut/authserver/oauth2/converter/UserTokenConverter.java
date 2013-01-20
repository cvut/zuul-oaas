package cz.cvut.authserver.oauth2.converter;

import java.util.Map;
import org.springframework.security.core.Authentication;

/**
* @author Dave Syer
*
*/
public interface UserTokenConverter {
    
    /**
     * Extract information about the user to be used in an access token (i.e.
     * for resource servers).
     *     
     * @param userAuthentication an authentication representing a user
     * @return a map of key values representing the unique information about the
     * user
     */
    Map<String, ?> convertUserAuthentication(Authentication userAuthentication);
}
