package cz.cvut.zuul.oaas.restapi.controllers

import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.api.services.ResourcesService

class ResourcesControllerIT extends AbstractControllerIT {

    def service = Mock(ResourcesService)

    def initController() { new ResourcesController() }
    void setupController(_) { _.resourceService = service }


    def 'GET all resources'() {
        setup:
            1 * service.getAllResources() >> [ build(ResourceDTO) ] * 3
        when:
            perform GET('/')
        then:
            with (response) {
                status      == 200
                contentType == CONTENT_TYPE_JSON
                json.size() == 3
            }
    }

    def 'GET resource'() {
        setup:
            1 * service.findResourceById('123') >> build(ResourceDTO)
        when:
            perform GET('/123')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
    }

    def 'POST resource'() {
        setup:
            service.createResource(_ as ResourceDTO) >> '123'
        when:
            perform POST ('/').with {
                content '{ "name": "Sample app" }'
            }
        then:
            response.status        == 201
            response.redirectedUrl == "${baseUri}/123"
    }

    def 'PUT resource with changed resourceId'() {
        when:
            perform PUT('/123').with {
                content '{ "resource_id": "666" }'
            }
        then:
            response.status == 409
    }

    def 'PUT resource'() {
        setup:
            1 * service.updateResource({
                it.name == 'Foobar'
            })
        when:
            perform PUT('123').with {
                content """{
                        "resource_id": "123",
                        "name": "Foobar"
                    }"""
            }
        then:
            response.status == 204
    }

    def 'DELETE resource'() {
        setup:
            1 * service.deleteResourceById('123')
        when:
            perform DELETE('/123')
        then:
            response.status == 204
    }
}
