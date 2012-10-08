package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.client_details.*;

/**
 * Converter from {@link ClientDetails} to MongoDB object.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ClientDetailsWriteConverter implements Converter<ClientDetails, DBObject> {

    public DBObject convert(ClientDetails source) {
        DBObject dbo = new BasicDBObject();
        
        dbo.put(CLIENT_ID, source.getClientId());
        dbo.put(CLIENT_SECRET, source.getClientSecret());
        dbo.put(SCOPE, source.getScope());
        dbo.put(RESOURCE_IDS, source.getResourceIds());
        dbo.put(AUTHORIZED_GRANT_TYPES, source.getAuthorizedGrantTypes());
        dbo.put(REDIRECT_URI, source.getRegisteredRedirectUri());
        dbo.put(AUTHORITIES, AuthorityUtils.authorityListToSet(source.getAuthorities()));
        dbo.put(ACCESS_TOKEN_VALIDITY, source.getAccessTokenValiditySeconds());
        dbo.put(REFRESH_TOKEN_VALIDITY, source.getRefreshTokenValiditySeconds());
        dbo.put(ADDITIONAL_INFORMATION, source.getAdditionalInformation());

        return dbo;
    }
}
