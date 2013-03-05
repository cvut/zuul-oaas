package cz.cvut.authserver.oauth2.dao.mongo.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.dao.mongo.converters.MongoDbConstants.authz_request;
import cz.cvut.authserver.oauth2.dao.mongo.converters.MongoDbConstants.user_auth;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import static cz.cvut.authserver.oauth2.dao.mongo.converters.MongoDbConstants.authentication.*;

/**
 * Converter from {@link OAuth2Authentication} to MongoDB object.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Component
public class OAuth2AuthenticationWriteConverter extends AutoRegisteredConverter<OAuth2Authentication, DBObject> {

    public DBObject convert(OAuth2Authentication source) {
        DBObject target = new BasicDBObject();

        target.put(AUTHORIZATION_REQUEST, convertAuthorizationRequest(source.getAuthorizationRequest()));
        target.put(USER_AUTHENTICATION, convertUserAuthentication(source.getUserAuthentication()));

        return target;
    }
    

    private DBObject convertAuthorizationRequest(AuthorizationRequest source) {
        DBObject target = new BasicDBObject();

        target.put(authz_request.APPROVAL_PARAMS, source.getApprovalParameters());
        target.put(authz_request.APPROVED, source.isApproved());
        target.put(authz_request.AUTHORITIES, AuthorityUtils.authorityListToSet(source.getAuthorities()));
        target.put(authz_request.AUTHZ_PARAMS, source.getAuthorizationParameters());
        target.put(authz_request.RESOURCE_IDS, source.getResourceIds());
        target.put("client_id", source.getClientId());

        return target;
    }

    private DBObject convertUserAuthentication(Authentication source) {
        if (source == null) return null;
        DBObject target = new BasicDBObject();

        //TODO incomplete!
        target.put(user_auth.USER_NAME, source.getName());
        target.put(user_auth.AUTHORITIES, AuthorityUtils.authorityListToSet(source.getAuthorities()));

        return target;
    }
}
