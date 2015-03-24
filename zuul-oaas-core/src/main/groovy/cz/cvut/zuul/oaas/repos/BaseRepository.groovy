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
package cz.cvut.zuul.oaas.repos

/**
 * Custom interface for generic CRUD operations on a repository for
 * a specific type.
 *
 * <p><i>Why Spring's {@link org.springframework.data.repository.CrudRepository
 * CrudRepository} is not used? Well, the problem is that this interface
 * doesn't work nicely with Groovy due to overuse of overloaded methods with
 * generic types. Groovy handles generics a bit differently than Java and these
 * methods are quite ambiguous here. This problem can be solved, but I found it
 * cleaner and more reliable to not use it and rather define custom interface
 * (I'm not using Repository support as intended anyway).
 * </i></p>
 *
 * @param <T> The domain type the repository manages.
 * @param <ID> The type of the id of the entity the repository manages.
 */
interface BaseRepository<T, ID extends Serializable> {

    /**
     * Saves a given entity. Use the returned instance for further operations
     * as the save operation might have changed the entity instance completely.
     *
     * @param entity
     * @return the saved entity
     */
    T save(T entity)

    /**
     * Saves all given entities.
     *
     * @param entities
     * @return the saved entities
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    List<T> saveAll(Iterable<? extends T> entities)

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found.
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    T findOne(ID id)

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return true if an entity with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    boolean exists(ID id)

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    List<T> findAll()

    /**
     * Returns all instances of the type with the given IDs.
     *
     * @param ids
     * @return
     */
    List<T> findAll(Iterable<ID> ids)

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities
     */
    long count()

    /**
     * Deletes a given entity.
     *
     * @param entity
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    void delete(T entity)

    /**
     * Deletes the given entities.
     *
     * @param entities
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    void deleteAll(Iterable<? extends T> entities)

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}.
     */
    void deleteById(ID id)
}
