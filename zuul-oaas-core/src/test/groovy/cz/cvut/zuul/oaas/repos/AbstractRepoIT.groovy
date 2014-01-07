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
package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.config.TestMongoPersistenceConfig
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.ParameterizedType

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

@ContextConfiguration(classes=TestMongoPersistenceConfig)
abstract class AbstractRepoIT<E> extends Specification {

    @Delegate CoreObjectFactory factory = new CoreObjectFactory()

    @Autowired MongoTemplate mongoTemplate


    //////// Setup ////////

    @Shared Class<E> entityClass = determineEntityClass()
    @Shared String idPropertyName = findIdProperty()
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

    abstract CrudRepository<E, ? extends Serializable> getRepo()


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
        assertThat(actual).equalsTo(expected).inAllProperties()
    }

    def ID(object) {
        object[idPropertyName]
    }

    private determineEntityClass() {
        (getClass().genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<E>
    }

    private findIdProperty() {
        getEntityClass().declaredFields.find {
            it.declaredAnnotations*.annotationType().contains(Id)
        }?.name
    }


    //////// Tests ////////

    def 'save a given entity'() {
        given:
            def entity = buildEntity()
        when:
            repo.save(entity)
        then:
            repo.count() == 1
    }

    def 'save all given entities'() {
        given:
            def entities = buildEntities(3)
        when:
            repo.save(entities)
        then:
            repo.count() == 3
    }

    def 'try to retrieve an entity by non existing id'() {
        setup:
            repo.save(seed())
        expect:
            ! repo.findOne('does-not-exist')
    }

    def 'retrieve an entity by the id'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)
        when:
            def actual = repo.findOne(ID(expected))
        then:
            assertIt actual, expected
    }

    def 'retrieve entities by the ids'() {
        setup:
            def expected = buildEntities(3)
            repo.save(expected)
            repo.save(seed())
        when:
            def actual = repo.findAll( expected.collect{ ID(it) } ).toList()
        then:
            actual.size() == expected.size()
    }

    def 'retrieve all entities'() {
        setup:
            def expected = buildEntities(3)
            repo.save(expected)
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
            repo.exists(ID(expected))
    }

    def 'try to delete entity by non existing id'() {
        setup:
            repo.save(seed())
        expect:
            repo.delete('does-not-exist')
    }

    def 'delete entity by the id'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)

            assert repo.findOne( ID(expected) )
        when:
            repo.delete(ID(expected))
        then:
            ! repo.findOne(ID(expected))
    }

    def 'delete a given entity'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)

            assert repo.findOne(ID(expected))
        when:
            repo.delete((E) expected)
        then:
            ! repo.findOne(ID(expected))
    }

    def 'delete all given entities'() {
        setup:
            def toPreserve = buildEntities(3)
            def toDelete = buildEntities(4)
            repo.save(toPreserve + toDelete)
        when:
            repo.delete((List<E>) toDelete)
        then:
            ! repo.findAll( toDelete.collect { ID(it) } )
            repo.findAll( toPreserve.collect { ID(it) } )
    }

    def 'delete all'() {
        setup:
            repo.save(buildEntities(3))
            assert repo.count() == 3
        when:
            repo.deleteAll()
        then:
            repo.count() == 0
    }
}
