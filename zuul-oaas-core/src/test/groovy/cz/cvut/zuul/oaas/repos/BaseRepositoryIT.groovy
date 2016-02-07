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

import org.springframework.data.domain.Persistable
import spock.lang.Specification

abstract class BaseRepositoryIT<E extends Persistable>
        extends Specification implements EntityTestSupport<E> {


    abstract BaseRepository<E, ? extends Serializable> getRepo()

    /**
     * Modify the given persistent entity for update testing.
     */
    abstract E modifyEntity(E entity)


    def 'try to retrieve an entity by non existing id'() {
        setup:
            repo.saveAll(seed())
        expect:
           ! repo.findOne(buildEntity().id)
    }

    def 'save, count, find and update entity'() {
        setup:
            def newEntity = buildEntity()
        and:
            assert repo.count() == 0, 'precondition not satisfied'
            assert !repo.findOne(newEntity.id), 'precondition not satisfied'

        when: 'save a new entity'
            repo.save(newEntity)
        then:
            repo.count() == 1
            assertIt repo.findOne(newEntity.id), newEntity

        when: 'update an existing entity'
            def updatedEntity = modifyEntity(repo.findOne(newEntity.id))
            repo.save(updatedEntity)
        then:
            repo.count() == 1
            assertIt repo.findOne(newEntity.id), updatedEntity
    }

    def 'save, count, find all and update multiple entities at once'() {
        setup:
            def newEntities = buildEntities(3)
        and:
            assert repo.count() == 0, 'precondition not satisfied'
            assert !repo.findAll(), 'precondition not satisfied'

        when: 'save new entities'
            repo.saveAll(newEntities)
        then:
            repo.count() == 3
            assertAll repo.findAll(), newEntities

        when: 'update existing entities'
            def updatedEntities = repo.findAll().collect { modifyEntity(it) }
            repo.saveAll(updatedEntities)
        then:
            repo.count() == 3
            assertAll repo.findAll(), updatedEntities
    }

    def 'retrieve entities by the ids'() {
        setup:
            def expected = buildEntities(3)
            repo.saveAll(expected)
            repo.saveAll(seed())
        when:
            def actual = repo.findAll( expected*.id )
        then:
            assertAll actual, expected
    }

    def 'check if any entity with the given id exists'() {
        setup:
            def expected = buildEntity()
            assert !repo.exists(expected.id), 'precondition not satisfied'
        when:
            repo.save(expected)
        then:
            repo.exists(expected.id)
    }

    def 'try to delete entity by non existing id'() {
        setup:
            repo.saveAll(seed())
            def count = repo.count()
        when:
            repo.deleteById(buildEntity().id)
        then:
            repo.count() == count
    }

    def 'delete entity by the id'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)
        and:
            assert repo.findOne(expected.id), 'precondition not satisfied'
        when:
            repo.deleteById(expected.id)
        then:
            ! repo.findOne(expected.id)
    }

    def 'delete a given entity'() {
        setup:
            def expected = buildEntity()
            repo.save(expected)
        and:
            assert repo.findOne(expected.id), 'precondition not satisfied'
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
        and:
            assert repo.findAll( toDelete*.id ).size() == 4, 'precondition not satisfied'
        when:
            repo.deleteAll((Iterable<E>) toDelete)
        then:
            ! repo.findAll( toDelete*.id )
            repo.findAll( toPreserve*.id )
    }
}
