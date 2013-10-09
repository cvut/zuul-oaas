package cz.cvut.zuul.oaas.restapi.controllers

import cz.cvut.zuul.oaas.api.models.TokenInfo
import cz.cvut.zuul.oaas.api.services.TokensService
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class CheckTokenEndpointIT extends AbstractControllerIT {

    def service = Mock(TokensService)

    def baseUri = '/check-token'
    def initController() { new CheckTokenEndpoint() }
    void setupController(_) { _.tokensService = service }


    def 'GET invalid token info'() {
        setup:
            1 * service.getTokenInfo('123') >> { throw new InvalidTokenException('foo') }
        when:
            perform GET('/?access_token={value}', '123')
        then:
            response.status == 409
    }

    def 'GET valid token info'() {
        setup:
            1 * service.getTokenInfo('123') >> expected
        when:
            perform GET('/?access_token={value}', '123')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
        where:
            expected = build(TokenInfo)
    }
}
