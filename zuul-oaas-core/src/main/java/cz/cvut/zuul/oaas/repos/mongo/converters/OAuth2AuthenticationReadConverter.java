package cz.cvut.zuul.oaas.repos.mongo.converters;

import com.mongodb.DBObject;
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authz_request;
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.user_auth;
import cz.cvut.zuul.oaas.models.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authentication.AUTHORIZATION_REQUEST;
import static cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authentication.USER_AUTHENTICATION;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

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
        target.setAuthorities( createAuthorityList(source.getStringArray(authz_request.AUTHORITIES)) );
        target.setResourceIds( source.getSet(authz_request.RESOURCE_IDS) );

        return target;
    }

    //TODO incomplete!
    private Authentication convertUserAuthentication(DBObjectWrapper source) {
        if (source == null) return null;

        Collection<GrantedAuthority> authorities = createAuthorityList(source.getStringArray(authz_request.AUTHORITIES));

        User user = new User(
                source.getString( user_auth.USER_NAME ),
                source.getString( user_auth.USER_EMAIL ),
                authorities
        );
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

}
