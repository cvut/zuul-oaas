package cz.cvut.zuul.oaas.services.converters

import ma.glasnost.orika.metadata.TypeFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Specification

class GrantedAuthorityConverterTest extends Specification {

    def converter = new GrantedAuthorityConverter()

    def 'convert from String to GrantedAuthority'() {
        given:
            def targetType = TypeFactory.valueOf(GrantedAuthority)
            def expected = new SimpleGrantedAuthority('Foo')
        expect:
            converter.convertFrom('Foo', targetType) == expected
    }

    def 'convert from GrantedAuthority to String'() {
        given:
            def targetType = TypeFactory.valueOf(String)
        expect:
            converter.convertTo(new SimpleGrantedAuthority('Foo'), targetType) == 'Foo'
    }
}
