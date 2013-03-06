package cz.cvut.authserver.oauth2.dao.jdbc;

import com.blogspot.nurkiewicz.jdbcrepository.RowUnmapper;
import cz.cvut.authserver.oauth2.dao.ResourceDAO;
import cz.cvut.authserver.oauth2.models.Resource;
import cz.cvut.authserver.oauth2.models.enums.Visibility;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cz.cvut.authserver.oauth2.models.Resource.fields.*;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 *
 * TODO add mapping for scopes!
 */
public class JdbcResourceDAO extends AbstractJdbcGenericDAO<Resource, String> implements ResourceDAO {

    public List<Resource> findAllPublic() {
        return findBy(VISIBILITY, Visibility.PUBLIC.toString());
    }

    
    //////// Mapping ////////
    
    protected RowMapper<Resource> getRowMapper() {
        return new RowMapper<Resource>() {
            public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
                Resource e = new Resource();

                e.setId( rs.getString(ID) );
                e.setBaseUrl( rs.getString(BASE_URL) );
                e.setDescription( rs.getString(DESCRIPTION) );
                e.setName( rs.getString(NAME) );
                e.setVersion( rs.getString(VERSION) );
                e.setTitle( rs.getString(TITLE) );
                e.setVisibility( rs.getString(VISIBILITY) );

                return e;
            }
        };
    }

    protected RowUnmapper<Resource> getRowUnmapper() {
        return new RowUnmapper<Resource>() {
            public Map<String, Object> mapColumns(final Resource e) {
                return new LinkedHashMap<String, Object>() {{
                    put(ID, e.getId());
                    put(BASE_URL, e.getBaseUrl());
                    put(DESCRIPTION, e.getDescription());
                    put(NAME, e.getName());
                    put(VERSION, e.getVersion());
                    put(TITLE, e.getTitle());
                    put(VISIBILITY, e.getVisibility());
                }};
            }
        };
    }

    protected String getTableName() {
        return "resources";
    }
}
