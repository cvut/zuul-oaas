package cz.cvut.zuul.oaas.repos.mongo.converters;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Component
public class GrantedAuthorityReaderConverter extends AutoRegisteredConverter<String, GrantedAuthority> {

    public GrantedAuthority convert(String role) {
        return new SimpleGrantedAuthority(role);
    }
}
