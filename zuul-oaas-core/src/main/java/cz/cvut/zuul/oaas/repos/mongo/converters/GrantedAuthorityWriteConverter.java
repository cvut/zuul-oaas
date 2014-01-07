package cz.cvut.zuul.oaas.repos.mongo.converters;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class GrantedAuthorityWriteConverter extends AutoRegisteredConverter<GrantedAuthority, String> {

    public String convert(GrantedAuthority source) {
        return source.getAuthority();
    }
}
