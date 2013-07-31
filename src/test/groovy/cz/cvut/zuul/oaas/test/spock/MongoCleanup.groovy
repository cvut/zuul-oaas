package cz.cvut.zuul.oaas.test.spock

import cz.cvut.zuul.oaas.test.spock.MongoCleanup.MongoCleanupExtension
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.ExtensionAnnotation
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.spring.SpringInterceptor
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.util.Assert

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Spock extension to cleanup Mongo database between tests.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(MongoCleanupExtension)
@interface MongoCleanup {

    /**
     * Domain classes with {@link org.springframework.data.mongodb.core.mapping.Document}
     * annotation to be inspected for the Mongo collections which should be
     * cleaned before and after each test invocation.
     *
     * When you don't provide any domain class or collection name, then all
     * collections in the opened Mongo database will be cleaned.
     */
    Class[] domains() default []

    /**
     * Names of Mongo collections that should be cleaned before and after each
     * test invocation.
     *
     * When you don't provide any domain class or collection name, then all
     * collections in the opened Mongo database will be cleaned.
     */
    String[] collections() default []


    static class MongoCleanupExtension extends AbstractAnnotationDrivenExtension<MongoCleanup> {

        @Override
        void visitSpecAnnotation(MongoCleanup annotation, SpecInfo spec) {
            def springInterceptor = spec.setupMethod.interceptors.find {
                it.class == SpringInterceptor
            }
            Assert.notNull(springInterceptor,
                    "No SpringInterceptor found, do you have @ContextConfiguration on your spec?")

            def springContext = findSpringApplicationContext(springInterceptor)
            if (!springContext) {
                throw new IllegalStateException("Could not beat out Spring's Application Context from Spock's SpringInterceptor")
            }

            def mongoTemplate
            try {
                mongoTemplate = springContext.getBean(MongoTemplate) as MongoTemplate
            } catch (NoSuchBeanDefinitionException ex) {
                throw new IllegalStateException("Cannot find MongoTemplate in Spring Context", ex)
            }

            def collections = annotation.collections() + annotation.domains()
            def interceptor = new MongoCleanupInterceptor(collections, mongoTemplate)

            spec.setupMethod.addInterceptor(interceptor)
            spec.cleanupMethod.addInterceptor(interceptor)
        }

        @SuppressWarnings("GroovyAccessibility")
        private findSpringApplicationContext(SpringInterceptor interceptor) {
            //beware of evil access...
            interceptor.@manager.@delegate.@testContext.applicationContext
        }
    }
}
