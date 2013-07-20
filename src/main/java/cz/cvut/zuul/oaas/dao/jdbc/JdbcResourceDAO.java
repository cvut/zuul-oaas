package cz.cvut.zuul.oaas.dao.jdbc;

import cz.cvut.zuul.oaas.dao.ResourceDAO;
import cz.cvut.zuul.oaas.models.Resource;
import cz.cvut.zuul.oaas.models.enums.Visibility;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static cz.cvut.zuul.oaas.models.Resource.fields.*;

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

    protected String getTableName() {
        return "resources";
    }

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

    protected Object[][] getEntityMapping(Resource e) {
        return new Object[][] {
                { ID,           e.getId()           },
                { BASE_URL,     e.getBaseUrl()      },
                { DESCRIPTION,  e.getDescription()  },
                { NAME,         e.getName()         },
                { VERSION,      e.getVersion()      },
                { TITLE,        e.getTitle()        },
                { VISIBILITY,   e.getVisibility()   }
        };
    }
}
