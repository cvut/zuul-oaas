package cz.cvut.authserver.oauth2.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import cz.cvut.authserver.oauth2.models.AuthenticatedAccessToken;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.access_tokens;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.authz_request;
import cz.cvut.authserver.oauth2.mongo.MongoDbConstants.user_auth;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class AuthenticatedAccessTokenWriteConverter implements Converter<AuthenticatedAccessToken, DBObject> {

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();


    public DBObject convert(AuthenticatedAccessToken source) {
        DBObject target = new BasicDBObject();

        OAuth2AccessToken token = source.getAccessToken();
        AuthorizationRequest authzRequest = source.getAuthentication().getAuthorizationRequest();
        Authentication userAuth = source.getAuthentication().getUserAuthentication();
        String authenticationKey = authenticationKeyGenerator.extractKey(source.getAuthentication());

        target.put(access_tokens.TOKEN_ID, token.getValue());
        target.put(access_tokens.EXPIRATION, token.getExpiration());
        target.put(access_tokens.TOKEN_TYPE, token.getTokenType());
        target.put(access_tokens.REFRESH_TOKEN, token.getRefreshToken() != null ? token.getRefreshToken().getValue() : null);
        target.put(access_tokens.SCOPE, token.getScope());
        target.put(access_tokens.ADDITIONAL_INFORMATION, token.getAdditionalInformation());

        target.put(access_tokens.AUTHORIZATION_REQUEST, convertAuthorizationRequest(authzRequest));
        target.put(access_tokens.USER_AUTHENTICATION, convertUserAuthentication(userAuth));

        target.put(access_tokens.CLIENT_ID, authzRequest.getClientId());
        target.put(access_tokens.AUTHENTICATION_KEY, authenticationKey);

        return target;
    }


    private DBObject convertAuthorizationRequest(AuthorizationRequest source) {
        DBObject target = new BasicDBObject();

        target.put(authz_request.APPROVAL_PARAMS, source.getApprovalParameters());
        target.put(authz_request.APPROVED, source.isApproved());
        target.put(authz_request.AUTHORITIES, AuthorityUtils.authorityListToSet(source.getAuthorities()));
        target.put(authz_request.AUTHZ_PARAMS, source.getAuthorizationParameters());
        target.put(authz_request.RESOURCE_IDS, source.getResourceIds());

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


    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }
}
