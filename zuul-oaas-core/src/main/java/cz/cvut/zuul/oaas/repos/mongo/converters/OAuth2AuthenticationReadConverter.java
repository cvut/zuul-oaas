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

import com.mongodb.DBObject;
import cz.cvut.zuul.oaas.models.User;
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authz_request;
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.user_auth;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collection;

import static cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authentication.AUTHORIZATION_REQUEST;
import static cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.authentication.USER_AUTHENTICATION;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

/**
 * Converter from MongoDB object to {@link OAuth2Authentication}.
 */
public class OAuth2AuthenticationReadConverter implements Converter<DBObject, OAuth2Authentication> {

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
