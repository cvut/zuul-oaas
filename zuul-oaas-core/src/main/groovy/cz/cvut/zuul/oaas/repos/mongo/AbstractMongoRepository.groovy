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

import cz.cvut.zuul.oaas.repos.BaseRepository
import groovy.transform.CompileStatic
import org.springframework.data.mapping.model.MappingException
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository
import org.springframework.util.Assert

import java.lang.reflect.ParameterizedType

/**
 * Abstract implementation of the {@link BaseRepository} for MongoDB that
 * delegates all common methods to Spring’s {@link SimpleMongoRepository}.
 */
@CompileStatic
abstract class AbstractMongoRepository<T, ID extends Serializable> implements BaseRepository<T, ID> {

    protected final MongoOperations mongo
    protected final Class<T> entityClass

    private final MongoRepository<T, ID> repository
    private final MongoEntityInformation<T, ID> entityInformation


    protected AbstractMongoRepository(MongoOperations mongoOperations) {
        this.mongo = mongoOperations
        this.entityClass = (Class<T>) ((ParameterizedType) this.class.genericSuperclass).actualTypeArguments[0]

        this.entityInformation = createEntityInformation(entityClass, mongoOperations)
        this.repository = new SimpleMongoRepository(entityInformation, mongoOperations)
    }


    //////// Delegate to MongoRepository ////////

    def T save(T entity) {
        repository.save(entity)
    }

    def List<T> saveAll(Iterable<? extends T> entites) {
        repository.save(entites)
    }

    T findOne(ID id) {
        repository.findOne(id)
    }

    boolean exists(ID id) {
        repository.exists(id)
    }

    List<T> findAll() {
        repository.findAll()
    }

    List<T> findAll(Iterable<ID> ids) {
        repository.findAll(ids) as List
    }

    long count() {
        repository.count()
    }

    void delete(T entity) {
        // Simple call to mongoRepository.delete(entity) doesn't work here due
        // to a type ambiguity problem.
        Assert.notNull(entity, 'The given entity must not be null!')

        repository.delete(entityInformation.getId(entity))
    }

    void deleteAll(Iterable<? extends T> entities) {
        repository.delete(entities)
    }

    void deleteById(ID id) {
        repository.delete(id)
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
