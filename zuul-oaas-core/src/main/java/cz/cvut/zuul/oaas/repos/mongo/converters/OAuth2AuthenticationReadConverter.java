/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.oauth_request;
import cz.cvut.zuul.oaas.repos.mongo.converters.MongoDbConstants.user_auth;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

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

        OAuth2Request authzReq = convertAuthorizationRequest( dbo.getDBObject(AUTHORIZATION_REQUEST) );
        Authentication userAuth = convertUserAuthentication( dbo.getDBObject(USER_AUTHENTICATION) );

        return new OAuth2Authentication(authzReq, userAuth);
    }


    @SuppressWarnings("unchecked")
    private OAuth2Request convertAuthorizationRequest(DBObjectWrapper source) {
        if (source == null) return null;

        return new OAuth2Request(
            source.getMap(oauth_request.REQUEST_PARAMS),
            source.getString(oauth_request.CLIENT_ID),
            createAuthorityList(source.getStringArray(oauth_request.AUTHORITIES)),
            source.getBoolean(oauth_request.APPROVED),
            source.getSet(oauth_request.SCOPE),
            source.getSet(oauth_request.RESOURCE_IDS),
            source.getString(oauth_request.REDIRECT_URI),
            source.getSet(oauth_request.RESPONSE_TYPES),
            source.getMap(oauth_request.EXTENSIONS)
        );
    }

    //TODO incomplete!
    private Authentication convertUserAuthentication(DBObjectWrapper source) {
        if (source == null) return null;

        Collection<GrantedAuthority> authorities = createAuthorityList(source.getStringArray(oauth_request.AUTHORITIES));

        User user = new User(
                source.getString( user_auth.USER_NAME ),
                source.getString( user_auth.USER_EMAIL ),
                authorities
        );
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

}
