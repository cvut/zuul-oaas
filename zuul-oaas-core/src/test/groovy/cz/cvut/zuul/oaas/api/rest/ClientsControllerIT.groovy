package cz.cvut.zuul.oaas.api.rest

import cz.cvut.zuul.oaas.api.exceptions.NoSuchClientException
import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.api.services.ClientsService
import org.hibernate.validator.method.MethodConstraintViolationException

import static java.util.Collections.emptySet
import static org.springframework.http.MediaType.APPLICATION_JSON

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ClientsControllerIT extends AbstractControllerIT {

    def service = Mock(ClientsService)

    def initController() { new ClientsController() }
    void setupController(_) { _.clientsService = service }


    void 'GET: non existing client'() {
        setup:
            1 * service.findClientById('666') >> { throw new NoSuchClientException('') }
        when:
            perform GET('/666').with {
                accept APPLICATION_JSON
            }
        then:
            response.status == 404
    }

    void 'GET: existing client'() {
        setup:
            1 * service.findClientById(expected.clientId) >> expected
        when:
            perform GET('/42').with {
                accept APPLICATION_JSON
            }
        then:
            with(response) {
                status == 200
                contentType == CONTENT_TYPE_JSON

                json.client_id              == expected.clientId
                json.client_secret          == expected.clientSecret
                json.resource_ids           == expected.resourceIds
                json.authorized_grant_types == expected.authorizedGrantTypes
                json.redirect_uri           == expected.registeredRedirectUri
                json.access_token_validity  == expected.accessTokenValiditySeconds
                json.refresh_token_validity == expected.refreshTokenValiditySeconds
                json.client_type            == expected.clientType
            }
        where:
            expected = build(ClientDTO, [clientId: '42'])
    }


    def 'POST: invalid client'() {
        setup:
            1 * service.createClient(_) >> { throw new MethodConstraintViolationException(emptySet()) }
        when:
            perform POST('/').with {
                content '{ "scope": "something" }'
                contentType APPLICATION_JSON
            }
        then:
            response.status == 400
    }

    def 'POST: valid client'() {
        setup:
            ClientDTO client
            service.createClient({ client = it }) >> '123'
        when:
            perform POST('/').with {
                contentType APPLICATION_JSON
                content """{
                    "authorities": [ "ROLE_CLIENT" ],
                    "authorized_grant_types": [ "refresh_token", "authorization_code" ],
                    "redirect_uri": [ "http://example.org" ],
                    "scope": [ "urn:ctu:oauth:dummy", "urn:ctu:oauth:oaas:check-token" ]
                }"""
            }
        then:
            response.status              == 201
            response.redirectedUrl       == "${baseUri}/123"

            client.authorities           == [ 'ROLE_CLIENT' ] as Set
            client.authorizedGrantTypes  == [ "refresh_token", "authorization_code" ] as Set
            client.registeredRedirectUri == [ "http://example.org" ] as Set
            client.scope                 == [ "urn:ctu:oauth:dummy", "urn:ctu:oauth:oaas:check-token" ] as Set
    }


    def 'PUT: non existing client'() {
        setup:
            1 * service.updateClient(_) >> { throw new NoSuchClientException('') }
        when:
            perform PUT('/666').with {
                contentType APPLICATION_JSON
                content """{
                        "client_id": "666"
                    }"""
            }
        then:
            response.status == 404
    }

    def 'PUT: client with changed clientId'() {
        when:
            perform PUT('/123').with {
                contentType APPLICATION_JSON
                content """{
                    "client_id": "666"
                }"""
            }
        then:
            response.status == 409
    }

    def 'PUT: valid client'() {
        setup:
            1 * service.updateClient({
                it.productName == 'Skynet'
            })
        when:
            perform PUT('/123').with {
                contentType APPLICATION_JSON
                content """{
                        "client_id": "123",
                        "product_name": "Skynet"
                    }"""
            }
        then:
            response.status == 204
    }


    def 'DELETE: non existing client'() {
        setup:
            1 * service.removeClient('666') >> { throw new NoSuchClientException('') }
        when:
            perform DELETE('/666')
        then:
            response.status == 404
    }

    def 'DELETE: existing client'() {
        when:
            perform DELETE('/123')
        then:
            response.status == 204
            1 * service.removeClient('123')
    }
}
