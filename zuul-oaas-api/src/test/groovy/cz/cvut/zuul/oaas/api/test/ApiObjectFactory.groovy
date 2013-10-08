package cz.cvut.zuul.oaas.api.test

import cz.cvut.oauth.provider.spring.TokenInfo
import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.test.ObjectFactory
import cz.cvut.zuul.oaas.test.ObjectFeeder
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyFixedValue
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString

/**
 * TODO refactor me!
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ApiObjectFactory extends ObjectFactory {

    ApiObjectFactory() {

        registerBuilder(ResourceDTO) { values ->
            def resource = new ResourceDTO(
                resourceId: anyLetterString(5, 10),
                baseUrl: 'http://example.org',
                description: anyLetterString(0, 255),
                name: anyLetterString(5, 255),
                version: anyLetterString(0, 255),
                visibility: anyFixedValue('public', 'hidden'),
                auth: new ResourceDTO.Auth(
                    scopes: buildListOf(ResourceDTO.Scope, 0, 3)
                )
            )
            values.each { prop, value ->
                resource[prop] = value
            }
            return resource
        }

        registerBuilder(TokenInfo) { values ->
            def token = new TokenInfo(
                clientAuthorities: buildListOf(GrantedAuthority),
                userAuthorities: buildListOf(GrantedAuthority)
            )
            ObjectFeeder.populate(token)
            values.each { prop, value ->
                token[prop] = value
            }
            return token
        }

        registerBuilder(SimpleGrantedAuthority) {
            new SimpleGrantedAuthority(
                anyFixedValue('ROLE_USER', 'ROLE_CLIENT', 'ROLE_ADMIN', 'ROLE_MASTER')
            )
        }

        registerSuperclass GrantedAuthority, SimpleGrantedAuthority
    }
}
