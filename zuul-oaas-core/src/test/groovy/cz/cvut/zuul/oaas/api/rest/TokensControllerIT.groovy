package cz.cvut.zuul.oaas.api.rest

import cz.cvut.zuul.oaas.api.exceptions.NoSuchTokenException
import cz.cvut.zuul.oaas.api.models.TokenDTO
import cz.cvut.zuul.oaas.api.services.TokensService

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class TokensControllerIT extends AbstractControllerIT {

    def service = Mock(TokensService)

    def initController() { new TokensController() }
    void setupController(_) { _.tokensService = service }


    void 'GET: existing token'() {
        setup:
            1 * service.getToken('42') >> token
        when:
            perform GET('/42')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
        where:
            token = build(TokenDTO)
    }

    void 'GET: non existing token'() {
        setup:
            1 * service.getToken('666') >> { throw new NoSuchTokenException('') }
        when:
            perform GET('/666')
        then:
            response.status == 404
    }


    void 'DELETE: existing token'() {
        setup:
            1 * service.invalidateToken('42')
        when:
            perform DELETE('/42')
        then:
            response.status == 204
    }

    void 'DELETE: non existing token'() {
        setup:
            1 * service.invalidateToken('666') >> { throw new NoSuchTokenException('') }
        when:
            perform DELETE('/666')
        then:
            response.status == 404
    }
}
