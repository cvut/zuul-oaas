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
package cz.cvut.zuul.oaas.persistence

import com.nurkiewicz.jdbcrepository.JdbcRepository
import com.nurkiewicz.jdbcrepository.RowUnmapper
import com.nurkiewicz.jdbcrepository.sql.PostgreSqlGenerator
import com.nurkiewicz.jdbcrepository.sql.SqlGenerator
import cz.cvut.zuul.oaas.models.Timestamped
import cz.cvut.zuul.oaas.persistence.support.TimestampedWriter
import cz.cvut.zuul.oaas.repos.BaseRepository
import groovy.transform.CompileStatic
import org.springframework.data.domain.Persistable
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.RowMapper

import java.sql.ResultSet

@CompileStatic
abstract class AbstractJdbcRepository<E extends Persistable, ID extends Serializable>
        implements BaseRepository<E, ID>, RowMapper<E>, RowUnmapper<E> {

    // Names of Timestamped columns
    protected static final String \
        COL_CREATED_AT = 'created_at',
        COL_UPDATED_AT = 'updated_at'

    protected final JdbcOperations jdbc

    @Lazy  // This must be initialized *after* this class is completely initialized.
    private JdbcRepository<E, ID> repository = new JdbcRepositoryAdapter(this)


    AbstractJdbcRepository(JdbcOperations jdbcOperations) {
        this.jdbc = jdbcOperations
    }


    //////// Abstract Methods ////////

    abstract getTableName()

    def getIdColumnName() { 'id' }

    abstract E mapRow(Map row)


    //////// Template Methods ////////

    E mapRow(ResultSet resultSet, int rowNum) {

        def row = resultSet.toRowResult()
        def entity = mapRow(row)

        if (entity instanceof Timestamped) {
            TimestampedWriter.setCreatedAt(entity, row[COL_CREATED_AT] as Date)
            TimestampedWriter.setUpdatedAt(entity, row[COL_UPDATED_AT] as Date)
        }
        entity
    }

    void setSqlGenerator(SqlGenerator sqlGenerator) {
        repository.sqlGenerator = sqlGenerator
    }

    protected List<E> query(String sql, ...args) {
        jdbc.query(sql, args, this)
    }

    protected E queryOne(String sql, ...args) {
        def results = query(sql, args)
        results.empty ? null : results.first()
    }

    protected List<E> findBy(LinkedHashMap<String, ?> columns) {
        def columnNames = columns.keySet().toList()

        query "SELECT * FROM ${tableName} WHERE ${whereAnd(columnNames)}",
              columns.values() as Object[]
    }

    protected void deleteBy(LinkedHashMap<String, ?> columns) {
        def columnNames = columns.keySet().toList()

        jdbc.update "DELETE FROM ${tableName} WHERE ${whereAnd(columnNames)}",
                    columns.values() as Object[]
    }

    private String whereAnd(List<String> columnNames) {
        columnNames*.concat('= ?').join(' AND ')
    }


    //////// Delegate to JdbcRepository ////////

    // Note: @Delegate doesn't work in this case, perhaps because of generics.

    E save(E entity) {
        repository.save(entity)
    }

    List<E> saveAll(Iterable<? extends E> entities) {
        entities.collect { E e -> save(e) }
    }

    E findOne(ID id) {
        repository.findOne(id)
    }

    boolean exists(ID id) {
        repository.exists(id)
    }

    List<E> findAll() {
        repository.findAll()
    }

    List<E> findAll(Iterable<ID> ids) {
        repository.findAll(ids).toList()
    }

    long count() {
        repository.count()
    }

    void delete(E entity) {
        repository.delete(entity)
    }

    void deleteAll(Iterable<? extends E> entities) {
        for (E e : entities) { delete(e) }
    }

    void deleteById(ID id) {
        repository.delete(id)
    }


    private class JdbcRepositoryAdapter extends JdbcRepository<E, ID> {

        JdbcRepositoryAdapter(AbstractJdbcRepository parent) {
            super(parent, parent, (String) parent.tableName, (String) parent.idColumnName)

            this.jdbcOperations = parent.jdbc
            this.sqlGenerator = new PostgreSqlGenerator()  // default
        }
    }
}
