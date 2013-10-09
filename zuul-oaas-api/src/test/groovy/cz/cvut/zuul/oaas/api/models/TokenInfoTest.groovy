package cz.cvut.zuul.oaas.api.models

import cz.cvut.zuul.oaas.api.test.ApiObjectFactory
import groovy.json.JsonSlurper
import org.codehaus.jackson.map.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification

import static org.codehaus.jackson.map.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Mixin(ApiObjectFactory)
class TokenInfoTest extends Specification {

    @Shared mapper = new ObjectMapper(propertyNamingStrategy: CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)


    void 'should marshall to JSON'() {
        when:
            def output = mapper.writeValueAsString(input)
        then:
            with(new JsonSlurper().parseText(output)) {
                client_id                 == input.clientId
                scope as Set              == input.scope
                audience as Set           == input.audience
                client_authorities as Set == input.clientAuthorities*.authority as Set
                expires_in                == input.expiresIn
                user_id                   == input.userId
                user_email                == input.userEmail
                user_authorities as Set   == input.userAuthorities*.authority as Set
            }
        where:
            input = build(TokenInfo)
    }
}
