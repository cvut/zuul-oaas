package cz.cvut.zuul.oaas.dao

import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import cz.cvut.zuul.oaas.test.spock.MongoCleanup
import org.apache.commons.lang3.reflect.TypeUtils
import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@MongoCleanup
@ContextConfiguration('classpath:dao-test.xml')
abstract class AbstractDAO_IT<E> extends Specification {

    @Delegate
    ObjectFactory factory = new ObjectFactory()

    @Shared Class<E> _entityClass
    @Shared String _idPropertyName


    //////// Helper methods ////////

    abstract CrudRepository<E, ? extends Serializable> getDao()


    def E buildEntity() {
        build(getEntityClass())
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
        object[getIdPropertyName()]
    }

    def getEntityClass() {
        if (_entityClass == null) {
            def typeVar = AbstractDAO_IT.class.typeParameters[0]
            def typeArgs = TypeUtils.getTypeArguments(this.class, AbstractDAO_IT.class)
            _entityClass = typeArgs[typeVar]
        }
        return _entityClass
    }

    def getIdPropertyName() {
        if (_idPropertyName == null) {
            _idPropertyName = findIdProperty()
        }
        return _idPropertyName
    }

    def findIdProperty() {
        getEntityClass().declaredFields.find {
            it.declaredAnnotations*.annotationType().contains(Id)
        }?.name
    }


    //////// Tests ////////

    def 'save a given entity'() {
        given:
            def entity = buildEntity()
        when:
            dao.save(entity)
        then:
            dao.count() == 1
    }

    def 'save all given entities'() {
        given:
            def entities = buildEntities(3)
        when:
            dao.save(entities)
        then:
            dao.count() == 3
    }

    def 'try to retrieve an entity by non existing id'() {
        setup:
            dao.save(seed())
        expect:
            ! dao.findOne('does-not-exist')
    }

    def 'retrieve an entity by the id'() {
        setup:
            def expected = buildEntity()
            dao.save(expected)
        when:
            def actual = dao.findOne(ID(expected))
        then:
            assertIt actual, expected
    }

    def 'retrieve entities by the ids'() {
        setup:
            def expected = buildEntities(3)
            dao.save(expected)
            dao.save(seed())
        when:
            def actual = dao.findAll( expected.collect{ ID(it) } ).toList()
        then:
            actual.size() == expected.size()
    }

    def 'retrieve all entities'() {
        setup:
            def expected = buildEntities(3)
            dao.save(expected)
        when:
            def actual = dao.findAll().toList()
        then:
            actual.size() == expected.size()
    }

    def 'whether an entity with the id exists'() {
        setup:
            def expected = buildEntity()
            dao.save(expected)
        expect:
            dao.exists(ID(expected))
    }

    def 'try to delete entity by non existing id'() {
        setup:
            dao.save(seed())
        expect:
            dao.delete('does-not-exist')
    }

    def 'delete entity by the id'() {
        setup:
            def expected = buildEntity()
            dao.save(expected)

            assert dao.findOne( ID(expected) )
        when:
            dao.delete(ID(expected))
        then:
            ! dao.findOne(ID(expected))
    }

    def 'delete a given entity'() {
        setup:
            def expected = buildEntity()
            dao.save(expected)

            assert dao.findOne(ID(expected))
        when:
            dao.delete((E) expected)
        then:
            ! dao.findOne(ID(expected))
    }

    def 'delete all given entities'() {
        setup:
            def toPreserve = buildEntities(3)
            def toDelete = buildEntities(4)
            dao.save(toPreserve + toDelete)
        when:
            dao.delete((List<E>) toDelete)
        then:
            ! dao.findAll( toDelete.collect { ID(it) } )
            dao.findAll( toPreserve.collect { ID(it) } )
    }

    def 'delete all'() {
        setup:
            dao.save(buildEntities(3))
            assert dao.count() == 3
        when:
            dao.deleteAll()
        then:
            dao.count() == 0
    }
}
