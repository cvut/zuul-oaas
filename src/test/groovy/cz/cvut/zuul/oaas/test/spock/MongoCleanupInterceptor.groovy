package cz.cvut.zuul.oaas.test.spock

import groovy.util.logging.Slf4j
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.util.NotThreadSafe
import org.springframework.data.mongodb.core.MongoTemplate

import static org.spockframework.runtime.model.MethodKind.CLEANUP
import static org.spockframework.runtime.model.MethodKind.SETUP

/**
 * Spock interceptor for {@link MongoCleanup} annotation.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Slf4j
@NotThreadSafe
class MongoCleanupInterceptor implements IMethodInterceptor {

    private final Object[] collections
    private final MongoTemplate mongoTemplate


    MongoCleanupInterceptor(Object[] collections, MongoTemplate mongoTemplate) {
        this.collections = collections
        this.mongoTemplate = mongoTemplate
    }

    void intercept(IMethodInvocation invocation) {
        switch (invocation.method.kind) {
            case SETUP:   cleanup(warn: true); break
            case CLEANUP: cleanup(); break
        }
        invocation.proceed()
    }

    void cleanup(opts = [:]) {
        if (collections) {
            collections.each { collection ->
                if (mongoTemplate.collectionExists(collection)) {
                    if (opts['warn']) log.warn "Collection $collection is not empty!"
                    log.info "Dropping collection: $collection"

                    mongoTemplate.dropCollection(collection)
                }
            }
        } else {
            if (mongoTemplate.collectionNames) {
                if (opts['warn']) log.warn "Database ${mongoTemplate.db.name} is not empty!"
                log.info "Dropping database: ${mongoTemplate.db.name}"

                mongoTemplate.db.dropDatabase()
            }
        }
    }
}
