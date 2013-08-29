package cz.cvut.zuul.oaas.services.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class GrantedAuthorityConverter extends BidirectionalConverter<GrantedAuthority, String> {

    public String convertTo(GrantedAuthority source, Type<String> destinationType) {
        return source.toString();
    }

    public GrantedAuthority convertFrom(String source, Type<GrantedAuthority> destinationType) {
        return new SimpleGrantedAuthority(source);
    }
}
