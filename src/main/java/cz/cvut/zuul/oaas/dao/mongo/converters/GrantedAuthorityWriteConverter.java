package cz.cvut.zuul.oaas.dao.mongo.converters;

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
