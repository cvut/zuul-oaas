package cz.cvut.authserver.oauth2.dao.mongo;

import cz.cvut.authserver.oauth2.utils.MongoUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Abstract implementation of the {@link CrudRepository} for MongoDB that
 * delegates all common methods to Spring’s {@link SimpleMongoRepository}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public abstract class AbstractMongoGenericDAO<T, ID extends Serializable> implements CrudRepository<T, ID> {

    private MongoOperations mongoOperations;

    private Class<T> entityClass;
    private MongoRepository<T, ID> mongoRepository;


    @PostConstruct
    protected void initialize() {
        entityClass = determineEntityClass();
        MongoEntityInformation<T, ID> entityInformation = MongoUtils.createEntityInformation(entityClass, mongoOperations);
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

    @SuppressWarnings("unchecked")
    private Class<T> determineEntityClass() {
        TypeVariable<?> typeVarE = AbstractMongoGenericDAO.class.getTypeParameters()[0];
        Type implType = this.getClass();

        return (Class<T>) TypeUtils.getTypeArguments(implType, AbstractMongoGenericDAO.class).get(typeVarE);
    }

    protected Class<T> entityClass() {
        return entityClass;
    }

    protected MongoOperations mongo() {
        return mongoOperations;
    }


    //////// Accessors ////////

    @Required
    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

}
