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
package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.exceptions.NoSuchResourceException;
import cz.cvut.zuul.oaas.api.models.ResourceDTO;
import cz.cvut.zuul.oaas.api.services.ResourcesService;
import cz.cvut.zuul.oaas.models.Resource;
import cz.cvut.zuul.oaas.models.Scope;
import cz.cvut.zuul.oaas.repos.ResourcesRepo;
import cz.cvut.zuul.oaas.services.converters.CaseInsensitiveToEnumConverter;
import cz.cvut.zuul.oaas.services.generators.StringEncoder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PACKAGE;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Service
@Setter @Slf4j
public class ResourcesServiceImpl implements ResourcesService {

    private ResourcesRepo resourcesRepo;

    /**
     * Encoder to be used to generate unique resourceId from the resource name.
     * When the generated identifier already exists, then the encoder is
     * invoked repeatedly until the identifier is unique.
     */
    private StringEncoder identifierEncoder;

    /**
     * Orika Mapper Factory to be configured and used for mapping between entity
     * and DTO objects. If no factory is provided, then new one will be created.
     *
     * @see {@link #setupMapper()}
     */
    private MapperFactory mapperFactory;

    @Setter(NONE) @Getter(PACKAGE)
    private MapperFacade mapper;



    public List<ResourceDTO> getAllResources() {
        return mapper.mapAsList(resourcesRepo.findAll(), ResourceDTO.class);
    }

    public List<ResourceDTO> getAllPublicResources() {
        return mapper.mapAsList(resourcesRepo.findAllPublic(), ResourceDTO.class);
    }

    public String createResource(ResourceDTO resourceDTO) {
        Resource resource = mapper.map(resourceDTO, Resource.class);

        String resourceId;
        do {
            log.debug("Generating unique resourceId");
            resourceId = identifierEncoder.encode(resource.getName());
        } while (resourcesRepo.exists(resourceId));

        resource.setId(resourceId);

        log.info("Creating new resource: [{}]", resource);
        resourcesRepo.save(resource);

        return resourceId;
    }

    public void updateResource(ResourceDTO resourceDTO) {
        log.info("Updating resource [{}]", resourceDTO);

        assertResourceExists(resourceDTO.getResourceId());
        resourcesRepo.save(mapper.map(resourceDTO, Resource.class));
    }

    public ResourceDTO findResourceById(String id) {
        Resource resource = resourcesRepo.findOne(id);

        if (resource == null) {
            throw new NoSuchResourceException("No such resource with id = %s", id);
        }
        return mapper.map(resource, ResourceDTO.class);
    }

    public void deleteResourceById(String id) {
        assertResourceExists(id);
        resourcesRepo.delete(id);
    }


    private void assertResourceExists(String resourceId) {
        if (! resourcesRepo.exists(resourceId)) {
            throw new NoSuchResourceException("No such resource with id = %s", resourceId);
        }
    }

    @PostConstruct void setupMapper() {
        MapperFactory factory = defaultIfNull(mapperFactory, new Builder().build());

        factory.getConverterFactory()
                .registerConverter(new CaseInsensitiveToEnumConverter());

        factory.classMap(Resource.class, ResourceDTO.class)
                .field("id", "resourceId")
                .field("scopes", "auth.scopes")
                .byDefault().register();

        factory.classMap(Scope.class, ResourceDTO.Scope.class)
                .byDefault().register();

        mapper = factory.getMapperFacade();
    }
}
