package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.refresh_tokens.*;

/**
 * Converter from MongoDB object to {@link PersistableRefreshToken}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class PersistableRefreshTokenReadConverter implements Converter<DBObject, PersistableRefreshToken> {

    public PersistableRefreshToken convert(DBObject source) {
        DBObjectWrapper dbo = new DBObjectWrapper(source);

        String value = dbo.getString(TOKEN_ID);
        Date expiration = dbo.getDate(EXPIRATION);

        PersistableRefreshToken target = new PersistableRefreshToken(value, expiration);

        DBObject authentication = dbo.getDBObject(AUTHENTICATION);
        if (authentication != null) {
            target.setAuthentication(new OAuth2AuthenticationReadConverter().convert(authentication));
        }

        return target;
    }
}
