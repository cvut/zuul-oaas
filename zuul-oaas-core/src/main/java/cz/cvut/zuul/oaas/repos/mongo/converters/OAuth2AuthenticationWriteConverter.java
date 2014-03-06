/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.repos.mongo.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authz_request;
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.user_auth;
import cz.cvut.zuul.oaas.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import static cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authentication.AUTHORIZATION_REQUEST;
import static cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authentication.USER_AUTHENTICATION;
import static org.springframework.security.core.authority.AuthorityUtils.authorityListToSet;

/**
 * Converter from {@link OAuth2Authentication} to MongoDB object.
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
        target.put(authz_request.AUTHORITIES, authorityListToSet(source.getAuthorities()));
        target.put(authz_request.AUTHZ_PARAMS, source.getAuthorizationParameters());
        target.put(authz_request.RESOURCE_IDS, source.getResourceIds());
        target.put(authz_request.CLIENT_ID, source.getClientId());

        return target;
    }

    private DBObject convertUserAuthentication(Authentication source) {
        if (source == null) return null;
        DBObject target = new BasicDBObject();

        //TODO incomplete!
        target.put(user_auth.USER_NAME, source.getName());
        target.put(user_auth.USER_EMAIL, getEmailFromPrincipal(source.getPrincipal()));
        target.put(user_auth.AUTHORITIES, authorityListToSet(source.getAuthorities()));

        return target;
    }
    
    private String getEmailFromPrincipal(Object principal) {
        if (principal instanceof User) {
            return ((User) principal).getEmail();
        }
        return null;
    }
}
