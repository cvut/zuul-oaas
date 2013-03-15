package cz.cvut.authserver.oauth2.dao.jdbc;

import com.blogspot.nurkiewicz.jdbcrepository.JdbcRepository;
import com.blogspot.nurkiewicz.jdbcrepository.RowUnmapper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of the {@link CrudRepository} for plain JDBC that
 * delegates all common methods to {@link JdbcRepository}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public abstract class AbstractJdbcGenericDAO<T extends Persistable<ID>, ID extends Serializable> implements CrudRepository<T, ID> {

    protected static final String ID = "id";

    private JdbcRepository<T, ID> jdbcRepository;
    private JdbcOperations jdbcOperations;


    @PostConstruct
    protected void initialize() {
        jdbcRepository = new JdbcRepository<T, ID>(getRowMapper(), getRowUnmapper(), getTableName(), getIdColumn(), jdbcOperations) {
            @Override
            public <S extends T> S save(S entity) {
                if (super.exists(entity.getId())) {
                    return update(entity);
                } else {
                    return create(entity);
                }
            }
        };
    }


    //////// Abstract methods ////////

    protected abstract String getTableName();

    protected abstract RowMapper<T> getRowMapper();

    protected abstract Object[][] getEntityMapping(T entity);


    protected RowUnmapper<T> getRowUnmapper() {
        return new RowUnmapper<T>() {
            public Map<String, Object> mapColumns(T t) {
                Map<String, Object> mapping = new LinkedHashMap<>();

                for (Object[] pair : getEntityMapping(t)) {
                    Assert.isTrue(pair.length == 2, "Inner array should have exactly two components");
                    Assert.isInstanceOf(String.class, pair[0], "First item of the inner array should be String");

                    mapping.put((String) pair[0], pair[1]);
                }
                return mapping;
            }
        };
    }

    protected String getIdColumn() { return ID; }


    //////// Delegate to JdbcRepository ////////

    public <S extends T> S save(S entity) {
        return jdbcRepository.save(entity);
    }

    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        return jdbcRepository.save(entities);
    }

    public T findOne(ID id) {
        return jdbcRepository.findOne(id);
    }

    public boolean exists(ID id) {
        return jdbcRepository.exists(id);
    }

    public Iterable<T> findAll() {
        return jdbcRepository.findAll();
    }

    public Iterable<T> findAll(Iterable<ID> ids) {
        return jdbcRepository.findAll(ids);
    }

    public long count() {
        return jdbcRepository.count();
    }

    public void delete(ID id) {
        jdbcRepository.delete(id);
    }

    public void delete(T entity) {
        jdbcRepository.delete(entity);
    }

    public void delete(Iterable<? extends T> entities) {
        jdbcRepository.delete(entities);
    }

    public void deleteAll() {
        jdbcRepository.deleteAll();
    }


    //////// Helpers ////////

    protected JdbcOperations jdbc() {
        return jdbcOperations;
    }

    protected List<T> findBy(String columnName, Object value) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?;", getTableName(), columnName);

        return jdbc().query(sql, getRowMapper(), value);
    }

    protected void deleteBy(String columnName, Object value) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?;", getTableName(), columnName);

        jdbc().update(sql, value);
    }


    //////// Accessors ////////

    @Required
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }
}
