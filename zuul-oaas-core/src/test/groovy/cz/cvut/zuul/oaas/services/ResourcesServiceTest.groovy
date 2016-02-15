/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.api.exceptions.NoSuchResourceException
import cz.cvut.zuul.oaas.repos.ResourcesRepo
import cz.cvut.zuul.oaas.services.generators.StringEncoder
import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

@Mixin(CoreObjectFactory)
class ResourcesServiceTest extends Specification {

    def resourcesRepo = Mock(ResourcesRepo)
    def idEncoder = Mock(StringEncoder)

    def service = new ResourcesServiceImpl(
            resourcesRepo: resourcesRepo,
            identifierEncoder: idEncoder
    )

    def setup() {
        service.setupMapper()
    }


    def 'create resource'() {
        given:
            def resource = build(ResourceDTO).with {
                it.resourceId = 'irrelevant'; return it
            }
            def generatedId = 'foo-123'
        when:
            def returnedId = service.createResource(resource)
        then:
            1 * idEncoder.encode(resource.name) >> generatedId
            1 * resourcesRepo.exists(generatedId) >> false
            1 * resourcesRepo.save({ Resource it ->
                it.id == generatedId
            })
            returnedId == generatedId
    }

    def 'create resource and handle generation of already taken id'() {
        given:
            def resource = build(ResourceDTO)
            def generatedId = 'foo-123'
        when:
            service.createResource(resource)
        then: 'generate unique id at the third attempt'
            3 * idEncoder.encode(resource.name) >>> ['taken-id', 'still-bad', generatedId]
            3 * resourcesRepo.exists(_) >>> [true, true, false]
        then:
            1 * resourcesRepo.save({ Resource it ->
                it.id == generatedId
            })
    }


    def 'update existing resource'() {
        when:
            service.updateResource( build(ResourceDTO) )
        then:
            1 * resourcesRepo.update(_ as Resource)
    }

    def 'update non existing resource'() {
        when:
            service.updateResource(build(ResourceDTO))
        then:
            resourcesRepo.update(_) >> {
                throw new IncorrectUpdateSemanticsDataAccessException('')
            }
            thrown NoSuchResourceException
    }


    def 'find non existing resource by id'() {
        when:
            service.findResourceById('foo-123')
        then:
            1 * resourcesRepo.findOne('foo-123') >> null
            thrown(NoSuchResourceException)
    }


    def 'delete non existing resource by id'() {
        when:
            service.deleteResourceById('non-existing')
        then:
            resourcesRepo.exists('non-existing') >> false
            thrown(NoSuchResourceException)
    }


    def 'map Resource to ResourceDTO'() {
        given:
            def resource = build(Resource)
        when:
            def dto = service.mapper.map(resource, ResourceDTO)
        then:
            assertMapping resource, dto
    }

    def 'map ResourceDTO to Resource'() {
        given:
            def dto = build(ResourceDTO)
        when:
            def resource = service.mapper.map(dto, Resource)
        then:
            assertMapping resource, dto
    }

    private void assertMapping(Resource entity, ResourceDTO dto) {
        assertThat( entity ).equalsTo( dto ).inAllPropertiesExcept( 'auth', 'resourceId', 'visibility' )
        assert entity.id == dto.resourceId
        assert entity.visibility.toString() == dto.visibility

        assertThat( entity.scopes.sort { it.name } )
            .equalsTo( dto.auth.scopes.sort { it.name } )
            .inAllProperties()
    }
}
