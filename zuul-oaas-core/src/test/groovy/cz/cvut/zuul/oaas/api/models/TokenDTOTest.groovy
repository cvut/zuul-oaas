package cz.cvut.zuul.oaas.api.models

import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import groovy.json.JsonSlurper
import org.codehaus.jackson.map.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification

import static org.codehaus.jackson.map.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Mixin(ObjectFactory)
class TokenDTOTest extends Specification {

    @Shared mapper = new ObjectMapper(propertyNamingStrategy: CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)


    def 'should marshall to JSON'() {
        when:
            def output = mapper.writeValueAsString(token)
        then:
            with (new JsonSlurper().parseText(output)) {
                expiration  == token.expiration?.time
                scope       == token.scope as List
                token_type  == token.tokenType
                token_value == token.tokenValue

                with (client_authentication) {
                    client_id     == clientAuth.clientId
                    client_locked == clientAuth.clientLocked
                    product_name  == clientAuth.productName
                    scope         == clientAuth.scope as List
                    redirect_uri  == clientAuth.redirectUri
                    resource_ids  == clientAuth.resourceIds as List
                }

                with (user_authentication) {
                    username      == userAuth.username
                    email         == userAuth.email
                    first_name    == userAuth.firstName
                    last_name     == userAuth.lastName
                }
            }
        where:
            token = build(TokenDTO).with {
                userAuthentication = build(TokenDTO.UserAuthentication)
                clientAuthentication = build(TokenDTO.ClientAuthentication)
                return it
            }
            userAuth = token.userAuthentication
            clientAuth = token.clientAuthentication
    }
}
