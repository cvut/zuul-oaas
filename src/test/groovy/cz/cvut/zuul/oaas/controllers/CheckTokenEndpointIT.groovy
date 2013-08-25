package cz.cvut.zuul.oaas.controllers

import cz.cvut.oauth.provider.spring.TokenInfo
import cz.cvut.zuul.oaas.api.resources.AbstractControllerIT
import cz.cvut.zuul.oaas.services.TokensService
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class CheckTokenEndpointIT extends AbstractControllerIT {

    def service = Mock(TokensService)

    def initController() { new CheckTokenEndpoint() }
    void setupController(_) { _.tokensService = service }


    def 'GET: invalid token info'() {
        setup:
            1 * service.getTokenInfo('123') >> { throw new InvalidTokenException('foo') }
        when:
            perform GET('/check-token?access_token={value}', '123')
        then:
            response.status == 409
    }

    def 'GET: valid token info'() {
        setup:
            1 * service.getTokenInfo('123') >> expected
        when:
            perform GET('/check-token?access_token={value}', '123')
        then:
            with(response) {
                status == 200
                contentType == CONTENT_TYPE_JSON

                json.client_id                 == expected.clientId
                json.scope as Set              == expected.scope
                json.audience as Set           == expected.audience
                json.client_authorities as Set == expected.clientAuthorities*.toString() as Set
                json.expires_in                == expected.expiresIn
                json.user_id                   == expected.userId
                json.user_email                == expected.userEmail
                json.user_authorities as Set   == expected.userAuthorities*.toString() as Set
            }
        where:
            expected = build(TokenInfo)
    }
}
