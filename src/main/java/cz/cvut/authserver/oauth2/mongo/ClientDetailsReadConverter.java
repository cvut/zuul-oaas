package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.mongo.DBObjectWrapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.client_details.*;

/**
 * Converter from MongoDB object to {@link ClientDetails}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ClientDetailsReadConverter implements Converter<DBObject, ClientDetails> {

    public ClientDetails convert(DBObject source) {
        BaseClientDetails target = new BaseClientDetails();
        DBObjectWrapper dbo = new DBObjectWrapper(source);

        target.setClientId( dbo.getString(CLIENT_ID) );
        target.setClientSecret( dbo.getString(CLIENT_SECRET) );
        target.setScope( dbo.getList(SCOPE) );
        target.setResourceIds( dbo.getList(RESOURCE_IDS) );
        target.setAuthorizedGrantTypes( dbo.getList(AUTHORIZED_GRANT_TYPES) );
        target.setRegisteredRedirectUri( dbo.getSet(REDIRECT_URI) );
        target.setAuthorities( AuthorityUtils.createAuthorityList( dbo.getStringArray(AUTHORITIES)) );
        target.setAccessTokenValiditySeconds( dbo.getInteger(ACCESS_TOKEN_VALIDITY) );
        target.setRefreshTokenValiditySeconds( dbo.getInteger(REFRESH_TOKEN_VALIDITY) );
        target.setAdditionalInformation( dbo.getMap(ADDITIONAL_INFORMATION) );

        return target;
    }

}
