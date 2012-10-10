package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.access_tokens.*;

/**
 * Converter from {@link PersistableAccessToken} to MongoDB object.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class PersistableAccessTokenWriteConverter implements Converter<PersistableAccessToken, DBObject> {

    //TODO inject via Spring
    private Converter<OAuth2Authentication, DBObject> authenticationConverter = new OAuth2AuthenticationWriteConverter();
    

    public DBObject convert(PersistableAccessToken source) {
        DBObject target = new BasicDBObject();

        target.put(TOKEN_ID, source.getValue());
        target.put(EXPIRATION, source.getExpiration());
        target.put(TOKEN_TYPE, source.getTokenType());
        target.put(REFRESH_TOKEN, source.getRefreshToken() != null ? source.getRefreshToken().getValue() : null);
        target.put(SCOPE, source.getScope());
        target.put(ADDITIONAL_INFORMATION, source.getAdditionalInformation());

        target.put(AUTHENTICATION, authenticationConverter.convert(source.getAuthentication()));
        target.put(AUTHENTICATION_KEY, source.getAuthenticationKey());
        target.put(CLIENT_ID, source.getAuthentication().getAuthorizationRequest().getClientId());

        if (! source.getAuthentication().isClientOnly()) {
            target.put(USER_NAME, source.getAuthentication().getUserAuthentication().getName());
        }

        return target;
    }
}
