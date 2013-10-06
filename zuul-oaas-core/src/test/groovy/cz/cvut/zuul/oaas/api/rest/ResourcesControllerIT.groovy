package cz.cvut.zuul.oaas.api.rest

import cz.cvut.zuul.oaas.api.exceptions.NoSuchResourceException
import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.api.services.ResourcesService
import org.hibernate.validator.method.MethodConstraintViolationException

import static java.util.Collections.emptySet

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ResourcesControllerIT extends AbstractControllerIT {

    def service = Mock(ResourcesService)

    def initController() { new ResourcesController() }
    void setupController(_) { _.resourceService = service }


    void 'GET: all resources'() {
        setup:
            1 * service.getAllResources() >> expected
        when:
            perform GET('/')
        then:
            with (response) {
                status      == 200
                contentType == CONTENT_TYPE_JSON
                json.size() == 3
            }
        where:
            expected = [build(ResourceDTO)] * 3
    }

    void 'GET: non existing resource'() {
        setup:
           1 * service.findResourceById('666') >> { throw new NoSuchResourceException('') }
        when:
            perform GET('/666')
        then:
            response.status == 404
    }

    void 'GET: existing resource'() {
        setup:
            1 * service.findResourceById(expected.resourceId) >> expected
        when:
            perform GET('/123')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
        where:
            expected = build(ResourceDTO, [resourceId: '123'])
    }


    def 'POST: invalid resource'() {
        setup:
            1 * service.createResource(_) >> { throw new MethodConstraintViolationException(emptySet()) }
        when:
            perform POST ('/').with {
                content '{ "name": "something" }'
            }
        then:
            response.status == 400
    }

    def 'POST: valid resource'() {
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


    def 'PUT: resource with changed resourceId'() {
        when:
            perform PUT('/123').with {
                content '{ "resource_id": "666" }'
            }
        then:
            response.status == 409
    }

    def 'PUT: valid resource'() {
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


    def 'DELETE: non existing resource'() {
        setup:
            1 * service.deleteResourceById('666') >> { throw new NoSuchResourceException("") }
        when:
            perform DELETE('/666')
        then:
            response.status == 404
    }

    def 'DELETE: existing resource'() {
        setup:
            1 * service.deleteResourceById('123')
        when:
            perform DELETE('/123')
        then:
            response.status == 204
    }
}
