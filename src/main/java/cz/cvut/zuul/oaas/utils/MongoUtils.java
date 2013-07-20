package cz.cvut.zuul.oaas.utils;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;

import java.io.Serializable;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public final class MongoUtils {
    private MongoUtils() {}

    /**
     * Returns the {@link org.springframework.data.repository.core.EntityInformation} for the given entity class.
     *
     * <p>Copied from {@link org.springframework.data.mongodb.repository.support.MongoRepositoryFactory#getEntityInformation(Class)}</p>
     *
     * @param <T> the entity type
     * @param <ID> the id type
     * @param entityClass
     * @param mongoOperations
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, ID extends Serializable>
            MongoEntityInformation<T, ID> createEntityInformation(Class<T> entityClass, MongoOperations mongoOperations) {

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext
                = mongoOperations.getConverter().getMappingContext();
        MongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(entityClass);

        if (entity == null) {
            throw new MappingException(String.format("Could not lookup mapping metadata for domain class %s!",
                    entityClass.getName()));
        }
        return new MappingMongoEntityInformation<>((MongoPersistentEntity<T>) entity);
    }

}
