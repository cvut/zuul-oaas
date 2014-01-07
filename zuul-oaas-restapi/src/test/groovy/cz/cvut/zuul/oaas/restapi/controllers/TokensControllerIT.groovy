package cz.cvut.zuul.oaas.restapi.controllers

import cz.cvut.zuul.oaas.api.models.TokenDTO
import cz.cvut.zuul.oaas.api.services.TokensService

class TokensControllerIT extends AbstractControllerIT {

    def service = Mock(TokensService)

    def initController() { new TokensController() }
    void setupController(_) { _.tokensService = service }


    def 'GET token'() {
        setup:
            1 * service.getToken('42') >> build(TokenDTO)
        when:
            perform GET('/42')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
    }

    def 'DELETE token'() {
        setup:
            1 * service.invalidateToken('42')
        when:
            perform DELETE('/42')
        then:
            response.status == 204
    }
}
