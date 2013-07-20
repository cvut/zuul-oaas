package cz.cvut.zuul.oaas.dao.jdbc;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import cz.cvut.zuul.oaas.dao.ClientDAO;
import cz.cvut.zuul.oaas.models.Client;
import cz.cvut.zuul.oaas.models.enums.AuthorizationGrant;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.net.URI;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static cz.cvut.zuul.oaas.models.Client.fields.*;
import static cz.cvut.zuul.oaas.utils.JdbcUtils.toArray;
import static cz.cvut.zuul.oaas.utils.JdbcUtils.toList;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class JdbcClientDAO extends AbstractJdbcGenericDAO<Client, String> implements ClientDAO {


    public void updateClientSecret(String clienId, String secret) throws EmptyResultDataAccessException {
        String sql = String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?", getTableName(), CLIENT_SECRET, ID);

        if (jdbc().update(sql, secret, clienId) == 0) {
            throw new EmptyResultDataAccessException("No row changed", 1);
        }
    }


    //////// Mapping ////////

    protected String getTableName() {
        return "clients";
    }

    protected RowMapper<Client> getRowMapper() {
        return new RowMapper<Client>() {
            public Client mapRow(final ResultSet rs, int rowNum) throws SQLException {
                Client e = new Client();

                e.setClientId( rs.getString(ID) );
                e.setClientSecret( rs.getString(CLIENT_SECRET) );
                e.setScope( toList(rs.getArray(SCOPE)) );
                e.setResourceIds( toList(rs.getArray(RESOURCE_IDS)) );
                e.setAuthorizedGrantTypes( arrayToAuthGrants(rs.getArray(AUTHORIZED_GRANT_TYPES)) );
                e.setAuthorities( arrayToAuthorities(rs.getArray(AUTHORITIES)) );
                e.setRegisteredRedirectUri( arrayToURIs(rs.getArray(REDIRECT_URI)) );
                e.setAccessTokenValiditySeconds( rs.getObject(ACCESS_TOKEN_VALIDITY, Integer.class) );
                e.setRefreshTokenValiditySeconds( rs.getObject(REFRESH_TOKEN_VALIDITY, Integer.class) );
                e.setProductName( rs.getString(PRODUCT_NAME) );
                e.setLocked( rs.getBoolean(LOCKED) );

                return e;
            }
        };
    }

    protected Object[][] getEntityMapping(Client e) {
        return new Object[][] {
                { ID,                       e.getClientId()                         },
                { CLIENT_SECRET,            e.getClientSecret()                     },
                { SCOPE,                    toArray(e.getScope())                   },
                { RESOURCE_IDS,             toArray(e.getResourceIds())             },
                { AUTHORIZED_GRANT_TYPES,   toArray(e.getAuthorizedGrantTypes())    },
                { REDIRECT_URI,             toArray(e.getRegisteredRedirectUri())   },
                { AUTHORITIES,              authoritiesToArray(e.getAuthorities())  },
                { ACCESS_TOKEN_VALIDITY,    e.getAccessTokenValiditySeconds()       },
                { REFRESH_TOKEN_VALIDITY,   e.getRefreshTokenValiditySeconds()      },
                { PRODUCT_NAME,             e.getProductName()                      },
                { LOCKED,                   e.isLocked()                            }
        };
    }

    private List<GrantedAuthority> arrayToAuthorities(Array sqlArray) throws SQLException {
        return Lists.transform(toList(sqlArray), new Function<String, GrantedAuthority>() {
            public GrantedAuthority apply(String input) {
                return new SimpleGrantedAuthority(input);
            }
        });
    }

    private Object[] authoritiesToArray(Collection<GrantedAuthority> authorities) {
        return toArray(AuthorityUtils.authorityListToSet(authorities));
    }

    private List<AuthorizationGrant> arrayToAuthGrants(Array sqlArray) throws SQLException {
        return AuthorizationGrant.valuesOf( toList(sqlArray) );
    }

    private List<URI> arrayToURIs(Array sqlArray) throws SQLException {
        return Lists.transform(toList(sqlArray), new Function<String, URI>() {
            public URI apply(String input) {
                return URI.create(input);
            }
        });
    }

}
