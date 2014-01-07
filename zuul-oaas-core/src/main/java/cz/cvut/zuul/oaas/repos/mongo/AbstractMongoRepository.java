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
package cz.cvut.zuul.oaas.repos.mongo;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Abstract implementation of the {@link CrudRepository} for MongoDB that
 * delegates all common methods to Spring’s {@link SimpleMongoRepository}.
 */
public abstract class AbstractMongoRepository<T, ID extends Serializable> implements CrudRepository<T, ID> {

    private MongoOperations mongoOperations;

    private Class<T> entityClass;
    private MongoRepository<T, ID> mongoRepository;



    @SuppressWarnings("unchecked")
    protected AbstractMongoRepository() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @PostConstruct void initialize() {
        MongoEntityInformation<T, ID> entityInformation = createEntityInformation(entityClass, mongoOperations);
        mongoRepository = new SimpleMongoRepository<>(entityInformation, mongoOperations);
    }


    //////// Delegate to MongoRepository ////////

    public <S extends T> S save(S entity) {
        return mongoRepository.save(entity);
    }

    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        return mongoRepository.save(entities);
    }

    public T findOne(ID id) {
        return mongoRepository.findOne(id);
    }

    public boolean exists(ID id) {
        return mongoRepository.exists(id);
    }

    public Iterable<T> findAll() {
        return mongoRepository.findAll();
    }

    public Iterable<T> findAll(Iterable<ID> ids) {
        return mongoRepository.findAll(ids);
    }

    public long count() {
        return mongoRepository.count();
    }

    public void delete(ID id) {
        mongoRepository.delete(id);
    }

    public void delete(T entity) {
        mongoRepository.delete(entity);
    }

    public void delete(Iterable<? extends T> entities) {
        mongoRepository.delete(entities);
    }

    public void deleteAll() {
        mongoRepository.deleteAll();
    }


    //////// Helpers ////////

    protected Class<T> entityClass() {
        return entityClass;
    }

    protected MongoOperations mongo() {
        return mongoOperations;
    }

    /**
     * Returns the {@link MappingMongoEntityInformation} for the given entity class.
     *
     * <p>Implementation copied from {@link org.springframework.data.mongodb.repository.support.MongoRepositoryFactory#getEntityInformation(Class)}</p>
     *
     * @param <T> the entity type
     * @param <ID> the id type
     * @param entityClass
     * @param mongoOperations
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T, ID extends Serializable>
            MongoEntityInformation<T, ID> createEntityInformation(Class<T> entityClass, MongoOperations mongoOperations) {

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext
                = mongoOperations.getConverter().getMappingContext();
        MongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(entityClass);

        if (entity == null) {
            throw new MappingException(String.format(
                    "Could not lookup mapping metadata for domain class %s!", entityClass.getName()));
        }
        return new MappingMongoEntityInformation<>((MongoPersistentEntity<T>) entity);
    }


    //////// Accessors ////////

    @Required
    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

}
