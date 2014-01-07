package cz.cvut.zuul.oaas.repos.mongo.converters;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class GrantedAuthorityReaderConverter extends AutoRegisteredConverter<String, GrantedAuthority> {

    public GrantedAuthority convert(String role) {
        return new SimpleGrantedAuthority(role);
    }
}
