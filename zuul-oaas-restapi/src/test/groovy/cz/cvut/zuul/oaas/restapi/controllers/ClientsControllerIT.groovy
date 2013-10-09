package cz.cvut.zuul.oaas.restapi.controllers

import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.api.services.ClientsService

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ClientsControllerIT extends AbstractControllerIT {

    def service = Mock(ClientsService)

    def initController() { new ClientsController() }
    void setupController(_) { _.clientsService = service }


    def 'GET client'() {
        setup:
            1 * service.findClientById('42') >> build(ClientDTO)
        when:
            perform GET('/42')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
    }

    def 'POST client'() {
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

    def 'PUT client with changed clientId'() {
        when:
            perform PUT('/123').with {
                content '{ "client_id": "666" }'
            }
        then:
            response.status == 409
    }

    def 'PUT client'() {
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

    def 'DELETE client'() {
        setup:
            1 * service.removeClient('123')
        when:
            perform DELETE('/123')
        then:
            response.status == 204
    }
}
