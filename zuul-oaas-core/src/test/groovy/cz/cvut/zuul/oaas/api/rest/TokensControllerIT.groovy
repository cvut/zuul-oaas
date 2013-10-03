package cz.cvut.zuul.oaas.api.rest

import cz.cvut.zuul.oaas.api.models.TokenDTO
import cz.cvut.zuul.oaas.api.exceptions.NoSuchTokenException
import cz.cvut.zuul.oaas.api.services.TokensService

import static org.springframework.http.MediaType.APPLICATION_JSON

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class TokensControllerIT extends AbstractControllerIT {

    def service = Mock(TokensService)

    def initController() { new TokensController() }
    void setupController(_) { _.tokensService = service }


    void 'GET: existing token'() {
        setup:
            token.clientAuthentication = clientAuth
            token.userAuthentication = userAuth

            1 * service.getToken('42') >> token
        when:
            perform GET('/42').with {
                accept APPLICATION_JSON
            }
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON

                with (json) {
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
            }
        where:
            userAuth   = build(TokenDTO.UserAuthentication)
            clientAuth = build(TokenDTO.ClientAuthentication)
            token      = build(TokenDTO)
    }

    void 'GET: non existing token'() {
        setup:
            1 * service.getToken('666') >> { throw new NoSuchTokenException('') }
        when:
            perform GET('/666').with {
                accept APPLICATION_JSON
            }
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
