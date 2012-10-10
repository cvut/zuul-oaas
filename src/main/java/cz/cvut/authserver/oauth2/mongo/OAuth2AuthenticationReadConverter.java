package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.authz_request;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.user_auth;
import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.authentication.*;

/**
 * Converter from MongoDB object to {@link OAuth2Authentication}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Component
public class OAuth2AuthenticationReadConverter extends AutoRegisteredConverter<DBObject, OAuth2Authentication> {

    public OAuth2Authentication convert(DBObject source) {
        DBObjectWrapper dbo = new DBObjectWrapper(source);

        AuthorizationRequest authzReq = convertAuthorizationRequest( dbo.getDBObject(AUTHORIZATION_REQUEST) );
        Authentication userAuth = convertUserAuthentication( dbo.getDBObject(USER_AUTHENTICATION) );
        
        return new OAuth2Authentication(authzReq, userAuth);
    }


    private AuthorizationRequest convertAuthorizationRequest(DBObjectWrapper source) {
        if (source == null) return null;

        DefaultAuthorizationRequest target = new DefaultAuthorizationRequest( source.getMap(authz_request.AUTHZ_PARAMS) );
        target.setApprovalParameters( source.getMap(authz_request.APPROVAL_PARAMS) );
        target.setApproved( source.getBoolean(authz_request.APPROVED) );
        target.setAuthorities( AuthorityUtils.createAuthorityList(source.getStringArray(authz_request.AUTHORITIES)) );
        target.setResourceIds( source.getSet(authz_request.RESOURCE_IDS) );

        return target;
    }

    //TODO incomplete!
    private Authentication convertUserAuthentication(DBObjectWrapper source) {
        if (source == null) return null;

        String username = source.getString( user_auth.USER_NAME );
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(source.getStringArray(authz_request.AUTHORITIES) );

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

}
