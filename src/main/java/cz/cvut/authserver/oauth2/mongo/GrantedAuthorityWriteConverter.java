package cz.cvut.authserver.oauth2.mongo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Component
public class GrantedAuthorityWriteConverter extends AutoRegisteredConverter<GrantedAuthority, String> {

    public String convert(GrantedAuthority source) {
        return source.getAuthority();
    }
}
