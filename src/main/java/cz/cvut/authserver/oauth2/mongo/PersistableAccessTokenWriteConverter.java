package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import org.springframework.stereotype.Component;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.access_tokens.*;

/**
 * Converter from {@link PersistableAccessToken} to MongoDB object.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Component
public class PersistableAccessTokenWriteConverter extends AutoRegisteredConverter<PersistableAccessToken, DBObject> {

    public DBObject convert(PersistableAccessToken source) {
        DBObject target = new BasicDBObject();

        target.put(TOKEN_ID, source.getValue());
        target.put(EXPIRATION, source.getExpiration());
        target.put(TOKEN_TYPE, source.getTokenType());
        target.put(REFRESH_TOKEN, source.getRefreshToken() != null ? source.getRefreshToken().getValue() : null);
        target.put(SCOPE, source.getScope());
        target.put(ADDITIONAL_INFORMATION, source.getAdditionalInformation());

        target.put(AUTHENTICATION, getConversionService().convert(source.getAuthentication(), DBObject.class));
        target.put(AUTHENTICATION_KEY, source.getAuthenticationKey());
        target.put(CLIENT_ID, source.getAuthentication().getAuthorizationRequest().getClientId());

        if (! source.getAuthentication().isClientOnly()) {
            target.put(USER_NAME, source.getAuthentication().getUserAuthentication().getName());
        }

        return target;
    }
}
