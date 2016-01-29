/*
 * The MIT License
 *
 * Copyright 2013-2016 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.config.TestMongoPersistenceConfig
import cz.cvut.zuul.oaas.models.Authenticated
import cz.cvut.zuul.oaas.models.Timestamped
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import cz.cvut.zuul.oaas.test.SharedAsserts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Persistable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.ParameterizedType

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

@ContextConfiguration(classes=TestMongoPersistenceConfig)
abstract class AbstractRepoIT<E extends Persistable> extends Specification {

    @Delegate CoreObjectFactory factory = new CoreObjectFactory()

    @Autowired MongoTemplate mongoTemplate


    //////// Setup ////////

    @Shared Class<E> entityClass = determineEntityClass()
    @Shared Class[] cleanup = [ entityClass ]


    def setup() {
        cleanup()
    }

    def cleanup() {
        getCleanup().each { entityClass ->
            mongoTemplate.dropCollection(entityClass)
        }
    }


    //////// Helper methods ////////

    abstract BaseRepository<E, ? extends Serializable> getRepo()


    def E buildEntity() {
        build(entityClass)
    }

    def List<E> buildEntities(count) {
        new Object[count].collect {
            buildEntity()
        }
    }

    def List<E> seed() {
        buildEntities(3)
    }

    void assertIt(E actual, E expected) {
        def excludedProps = ['new']

        if (Timestamped.isAssignableFrom(entityClass)) {
            excludedProps += ['createdAt', 'updatedAt']
        }
        if (Authenticated.isAssignableFrom(entityClass)) {
            excludedProps << 'authentication'
            SharedAsserts.isEqual(actual.authentication, expected.authentication)
        }
        assertThat( actual ).equalsTo( expected ).inAllPropertiesExcept( *excludedProps )
    }

    private determineEntityClass() {
        (getClass().genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<E>
    }


    //////// Tests ////////

    def 'save a new entity'() {
        given:
            def entity = buildEntity()
        when:
            repo.save(entity)
        then:
            repo.count() == 1
    }

    def 'save all new entities'() {
        given:
            def entities = buildEntities(3)
        when:
            repo.saveAll(entities)
        then:
            repo.count() == 3
    }

    def 'try to retrieve an entity by non existing id'() {
        setup:
            repo.saveAll(seed())
        expect:
            ! repo.findOne('does-not-exist')
    }

    def 'retrieve an entity by the id'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)
        when:
            def actual = repo.findOne(expected.id)
        then:
            assertIt actual, expected
    }

    def 'retrieve entities by the ids'() {
        setup:
            def expected = buildEntities(3)
            repo.saveAll(expected)
            repo.saveAll(seed())
        when:
            def actual = repo.findAll( expected.collect{ it.id } ).toList()
        then:
            actual.size() == expected.size()
    }

    def 'retrieve all entities'() {
        setup:
            def expected = buildEntities(3)
            repo.saveAll(expected)
        when:
            def actual = repo.findAll().toList()
        then:
            actual.size() == expected.size()
    }

    def 'whether an entity with the id exists'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)
        expect:
            repo.exists(expected.id)
    }

    def 'try to delete entity by non existing id'() {
        setup:
            repo.saveAll(seed())
        expect:
            repo.deleteById('does-not-exist')
    }

    def 'delete entity by the id'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)

            assert repo.findOne(expected.id)
        when:
            repo.deleteById(expected.id)
        then:
            ! repo.findOne(expected.id)
    }

    def 'delete a given entity'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)

            assert repo.findOne(expected.id)
        when:
            repo.delete(expected)
        then:
            ! repo.findOne(expected.id)
    }

    def 'delete all given entities'() {
        setup:
            def toPreserve = buildEntities(3)
            def toDelete = buildEntities(4)
            repo.saveAll(toPreserve + toDelete)
        when:
            repo.deleteAll((Iterable<E>) toDelete)
        then:
            ! repo.findAll( toDelete.collect { it.id } )
            repo.findAll( toPreserve.collect { it.id } )
    }
}
