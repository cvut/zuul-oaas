package cz.cvut.zuul.oaas.api.rest

import cz.cvut.zuul.oaas.api.exceptions.NoSuchClientException
import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.api.services.ClientsService
import org.hibernate.validator.method.MethodConstraintViolationException

import static java.util.Collections.emptySet

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
            perform GET('/666')
        then:
            response.status == 404
    }

    void 'GET: existing client'() {
        setup:
            1 * service.findClientById(expected.clientId) >> expected
        when:
            perform GET('/42')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
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
            }
        then:
            response.status == 400
    }

    def 'POST: valid client'() {
        setup:
            1 * service.createClient(_ as ClientDTO) >> '123'
        when:
            perform POST('/').with {
                content '{ "redirect_uri": "http://example.org" }'
            }
        then:
            response.status        == 201
            response.redirectedUrl == "${baseUri}/123"
    }


    def 'PUT: non existing client'() {
        setup:
            1 * service.updateClient(_) >> { throw new NoSuchClientException('') }
        when:
            perform PUT('/666').with {
                content '{ "client_id": "666" }'
            }
        then:
            response.status == 404
    }

    def 'PUT: client with changed clientId'() {
        when:
            perform PUT('/123').with {
                content '{ "client_id": "666" }'
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
        setup:
            1 * service.removeClient('123')
        when:
            perform DELETE('/123')
        then:
            response.status == 204
    }
}
