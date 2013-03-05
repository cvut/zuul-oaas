package cz.cvut.authserver.oauth2.dao.jdbc;

import com.blogspot.nurkiewicz.jdbcrepository.RowUnmapper;
import cz.cvut.authserver.oauth2.dao.RefreshTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static cz.cvut.authserver.oauth2.utils.JdbcUtils.deserialize;
import static cz.cvut.authserver.oauth2.utils.JdbcUtils.serialize;
import static cz.cvut.authserver.oauth2.models.PersistableRefreshToken.fields.*;


/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class JdbcRefreshTokenDAO extends AbstractJdbcGenericDAO<PersistableRefreshToken, String> implements RefreshTokenDAO {

    protected String getTableName() {
        return "refresh_tokens";
    }

    protected RowMapper<PersistableRefreshToken> getRowMapper() {
        return new RowMapper<PersistableRefreshToken>() {
            public PersistableRefreshToken mapRow(final ResultSet rs, int rowNum) throws SQLException {
                String value = rs.getString(ID);
                Date expiration = rs.getTimestamp(EXPIRATION);
                OAuth2Authentication authentication = deserialize(rs.getBytes(AUTHENTICATION), OAuth2Authentication.class);

                PersistableRefreshToken token = new PersistableRefreshToken(value, expiration);
                token.setAuthentication(authentication);

                return token;
            }
        };
    }

    protected RowUnmapper<PersistableRefreshToken> getRowUnmapper() {
        return new RowUnmapper<PersistableRefreshToken>() {
            public Map<String, Object> mapColumns(final PersistableRefreshToken e) {
                return new LinkedHashMap<String, Object>() {{
                    put(ID, e.getValue());
                    put(EXPIRATION, e.getExpiration());
                    put(AUTHENTICATION, serialize(e.getAuthentication()));
                }};
            }
        };
    }
}
