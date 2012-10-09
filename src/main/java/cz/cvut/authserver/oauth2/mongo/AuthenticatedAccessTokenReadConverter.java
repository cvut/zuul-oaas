package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.models.AuthenticatedAccessToken;
import cz.cvut.authserver.oauth2.mongo.DBObjectWrapper;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.access_tokens;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.authz_request;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.user_auth;
import java.util.Collection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class AuthenticatedAccessTokenReadConverter implements Converter<DBObject, AuthenticatedAccessToken> {

    public AuthenticatedAccessToken convert(DBObject source) {
        DBObjectWrapper dbo = new DBObjectWrapper(source);

        DefaultOAuth2AccessToken target = new DefaultOAuth2AccessToken( dbo.getString(access_tokens.TOKEN_ID) );
        target.setExpiration( dbo.getDate(access_tokens.EXPIRATION) );
        target.setTokenType( dbo.getString(access_tokens.TOKEN_TYPE) );
        //token.setRefreshToken( dbo.getString(access_token.REFRESH_TOKEN));
        target.setScope( dbo.getSet(access_tokens.SCOPE) );
        target.setAdditionalInformation( dbo.getMap(access_tokens.ADDITIONAL_INFORMATION) );

        AuthorizationRequest authzRequest = convertAuthorizationRequest( dbo.getDBObject(access_tokens.AUTHORIZATION_REQUEST) );
        Authentication userAuth = convertUserAuthentication( dbo.getDBObject(access_tokens.USER_AUTHENTICATION) );
        OAuth2Authentication authentication = new OAuth2Authentication(authzRequest, userAuth);

        return new AuthenticatedAccessToken(target, authentication);
    }


    private AuthorizationRequest convertAuthorizationRequest(DBObjectWrapper source) {
        if (source == null) return null;
        DefaultAuthorizationRequest target;

        target = new DefaultAuthorizationRequest( source.getMap(authz_request.AUTHZ_PARAMS) );
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
