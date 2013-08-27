package cz.cvut.zuul.oaas.api.resources

import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.api.resources.exceptions.NoSuchResourceException
import cz.cvut.zuul.oaas.services.ResourcesService
import org.hibernate.validator.method.MethodConstraintViolationException

import static java.util.Collections.emptySet
import static org.springframework.http.MediaType.APPLICATION_JSON

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
            perform GET('/v1/resources/').with {
                accept APPLICATION_JSON
            }
        then:
            with(response) {
                status      == 200
                contentType == CONTENT_TYPE_JSON

                json instanceof List
                json.size() == 3
            }
        where:
            expected = [build(ResourceDTO)] * 3
    }

    void 'GET: non existing resource'() {
        setup:
           1 * service.findResourceById('666') >> { throw new NoSuchResourceException('') }
        when:
            perform GET('/v1/resources/666').with {
                accept APPLICATION_JSON
            }
        then:
            response.status == 404
    }

    void 'GET: existing resource'() {
        setup:
            1 * service.findResourceById(expected.resourceId) >> expected
        when:
            perform GET('/v1/resources/123').with {
                accept APPLICATION_JSON
            }
        then:
            with(response) {
                status == 200
                contentType == CONTENT_TYPE_JSON

                json.resource_id        == expected.resourceId
                json.auth.scopes*.name  == expected.auth.scopes*.name
                json.base_url           == expected.baseUrl
                json.description        == expected.description
                json.name               == expected.name
                json.version            == expected.version
                json.visibility         == expected.visibility
            }
        where:
            expected = build(ResourceDTO, [resourceId: '123'])
    }


    def 'POST: invalid resource'() {
        setup:
            1 * service.createResource(_) >> { throw new MethodConstraintViolationException(emptySet()) }
        when:
            perform POST ('/v1/resources/').with {
                content '{ "name": "something" }'
                contentType APPLICATION_JSON
            }
        then:
            response.status == 400
    }

    def 'POST: valid resource'() {
        setup:
            ResourceDTO resource
            service.createResource({ resource = it }) >> '123'
        when:
            perform POST ('/v1/resources/').with {
                contentType APPLICATION_JSON
                content """{
                        "auth": {
                            "scopes":   [ { "name": "urn:ctu:sample" } ]
                        },
                        "base_url":     "http://example.org",
                        "description":  "Lorem ipsum",
                        "name":         "Sample app",
                        "version":      "1.0",
                        "visibility":   "public"
                    }"""
            }
        then:
            response.status         == 201
            response.redirectedUrl  == '/v1/resources/123'

            resource.auth.scopes*.name  == [ 'urn:ctu:sample' ]
            resource.baseUrl            == 'http://example.org'
            resource.description        == 'Lorem ipsum'
            resource.name               == 'Sample app'
            resource.version            == '1.0'
            resource.visibility         == 'public'
    }

    def 'PUT: invalid update'() {
        //TODO
    }

    def 'PUT: valid update'() {
        //TODO
    }

    def 'DELETE: non existing resource'() {
        setup:
            1 * service.deleteResourceById('666') >> { throw new NoSuchResourceException("") }
        when:
            perform DELETE('/v1/resources/666')
        then:
            response.status == 404
    }

    def 'DELETE: existing resource'() {
        when:
            perform DELETE('/v1/resources/123')
        then:
            response.status == 204
            1 * service.deleteResourceById('123')
    }
}
