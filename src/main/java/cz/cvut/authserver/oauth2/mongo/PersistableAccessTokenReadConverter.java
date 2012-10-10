package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.access_tokens.*;

/**
 * Converter from MongoDB object to {@link PersistableAccessToken}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Component
public class PersistableAccessTokenReadConverter extends AutoRegisteredConverter<DBObject, PersistableAccessToken> {

    public PersistableAccessToken convert(DBObject source) {
        DBObjectWrapper dbo = new DBObjectWrapper(source);

        PersistableAccessToken target = new PersistableAccessToken(dbo.getString(TOKEN_ID));
        target.setExpiration(dbo.getDate(EXPIRATION));
        target.setTokenType(dbo.getString(TOKEN_TYPE));
        target.setScope(dbo.getSet(SCOPE));
        target.setAdditionalInformation(dbo.getMap(ADDITIONAL_INFORMATION));

        String refreshToken = dbo.getString(REFRESH_TOKEN);
        if (refreshToken != null) {
            target.setRefreshToken(new DefaultOAuth2RefreshToken(refreshToken)); // TODO is this okay?
        }

        target.setAuthenticationKey(dbo.getString(AUTHENTICATION_KEY));

        DBObject authentication = dbo.getDBObject(AUTHENTICATION);
        if (authentication != null) {
            target.setAuthentication(getConversionService().convert(authentication, OAuth2Authentication.class));
        }

        return target;
    }
}
