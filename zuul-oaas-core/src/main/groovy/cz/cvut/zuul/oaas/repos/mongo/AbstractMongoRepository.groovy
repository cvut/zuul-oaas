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
package cz.cvut.zuul.oaas.repos.mongo

import org.springframework.data.mapping.model.MappingException
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository
import org.springframework.data.repository.CrudRepository

import java.lang.reflect.ParameterizedType

/**
 * Abstract implementation of the {@link CrudRepository} for MongoDB that
 * delegates all common methods to Spring’s {@link SimpleMongoRepository}.
 */
abstract class AbstractMongoRepository<T, ID extends Serializable> implements CrudRepository<T, ID> {

    protected final MongoOperations mongo

    protected final Class<T> entityClass

    @Delegate
    private final CrudRepository<T, ID> mongoRepository


    protected AbstractMongoRepository(MongoOperations mongoOperations) {
        this.mongo = mongoOperations
        this.entityClass = (Class<T>) ((ParameterizedType) this.class.genericSuperclass).actualTypeArguments[0]

        def entityInformation = createEntityInformation(entityClass, mongoOperations)
        this.mongoRepository = new SimpleMongoRepository(entityInformation, mongoOperations)
    }


    // There's some type problem with these methods to be delegated,
    // we must implement them explicitly.

    def <S extends T> S save(S entity) {
        (S) mongoRepository.save(entity)
    }

    def <S extends T> Iterable<S> save(Iterable<S> entites) {
        mongoRepository.save(entites)
    }


    /**
     * Returns the {@link MappingMongoEntityInformation} for the given entity class.
     *
     * <p>Implementation copied from {@link org.springframework.data.mongodb.repository.support.MongoRepositoryFactory#getEntityInformation(Class)}</p>
     */
    private MongoEntityInformation<T, ID> createEntityInformation(Class<T> entityClass, MongoOperations mongoOperations) {

        def entity = mongoOperations.converter.mappingContext.getPersistentEntity(entityClass)

        if (!entity) {
            throw new MappingException("Could not lookup mapping metadata for domain class ${entityClass.name}!")
        }
        new MappingMongoEntityInformation<T, ID>((MongoPersistentEntity<T>) entity)
    }
}
