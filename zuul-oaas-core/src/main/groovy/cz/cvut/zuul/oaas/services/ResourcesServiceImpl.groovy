/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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

import cz.cvut.zuul.oaas.api.exceptions.NoSuchResourceException
import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.api.services.ResourcesService
import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.models.Scope
import cz.cvut.zuul.oaas.repos.ResourcesRepo
import cz.cvut.zuul.oaas.services.converters.CaseInsensitiveToEnumConverter
import cz.cvut.zuul.oaas.services.generators.StringEncoder
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

import static cz.cvut.zuul.oaas.common.Looper.loop

@Service @Slf4j
class ResourcesServiceImpl implements ResourcesService {

    ResourcesRepo resourcesRepo

    /**
     * Encoder to be used to generate unique resourceId from the resource name.
     * When the generated identifier already exists, then the encoder is
     * invoked repeatedly until the identifier is unique.
     */
    StringEncoder identifierEncoder

    /**
     * Orika Mapper Factory to be configured and used for mapping between entity
     * and DTO objects. If no factory is provided, then new one will be created.
     *
     * @see {@link #setupMapper()}
     */
    private MapperFactory mapperFactory

    @PackageScope MapperFacade mapper



    List<ResourceDTO> getAllResources() {
        mapper.mapAsList(resourcesRepo.findAll(), ResourceDTO)
    }

    List<ResourceDTO> getAllPublicResources() {
        mapper.mapAsList(resourcesRepo.findAllPublic(), ResourceDTO)
    }

    String createResource(ResourceDTO resourceDTO) {

        def resource = mapper.map(resourceDTO, Resource)

        resource.id = loop {
            log.debug 'Generating unique resourceId'
            identifierEncoder.encode(resource.name)
        } until { id, i -> !resourcesRepo.exists(id) || i > 10 }

        if (!resource.id) {
            throw new IllegalStateException('Failed to generate resource id')
        }

        log.info 'Creating new resource: [{}]', resource
        resourcesRepo.save(resource)

        resource.id
    }

    void updateResource(ResourceDTO resourceDTO) {
        log.info 'Updating resource [{}]', resourceDTO

        assertResourceExists resourceDTO.resourceId

        resourcesRepo.save(mapper.map(resourceDTO, Resource))
    }

    ResourceDTO findResourceById(String id) {
        def resource = resourcesRepo.findOne(id)

        if (!resource) {
            throw new NoSuchResourceException("No such resource with id = ${id}")
        }
        mapper.map(resource, ResourceDTO)
    }

    void deleteResourceById(String id) {
        assertResourceExists id

        resourcesRepo.delete(id)
    }


    private assertResourceExists(String resourceId) {
        if (!resourcesRepo.exists(resourceId)) {
            throw new NoSuchResourceException("No such resource with id = ${resourceId}")
        }
    }

    @PostConstruct setupMapper() {
        def factory = mapperFactory ?: new Builder().build()

        factory.converterFactory
                .registerConverter(new CaseInsensitiveToEnumConverter())

        factory.classMap(Resource, ResourceDTO)
                .field('id', 'resourceId')
                .field('scopes', 'auth.scopes')
                .byDefault().register()

        factory.classMap(Scope, ResourceDTO.Scope)
                .byDefault().register()

        mapper = factory.mapperFacade
    }
}
