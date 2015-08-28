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
package cz.cvut.zuul.oaas.repos.mongo

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import cz.cvut.zuul.oaas.common.ConverterAdapter
import cz.cvut.zuul.oaas.models.User
import groovy.transform.CompileStatic
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request

import java.lang.reflect.Field

import static cz.cvut.zuul.oaas.common.ConverterAdapter.createConverter
import static org.springframework.security.core.authority.AuthorityUtils.authorityListToSet

/**
 * Custom converters from/to Mongo's {@link DBObject}.
 *
 * <p><i>Note: This class is just a collection of constants, it's not intended to
 * be used as actual interface! Interface is more convenient than (abstract)
 * class for this.</i></p>
 */
@CompileStatic
@SuppressWarnings('GroovyUnusedDeclaration')
interface CustomConverters {

    ConverterAdapter OAuth2AuthenticationToDBObject = createConverter OAuth2Authentication, DBObject, {
        new BasicDBObject([
            oauthReq: OAuth2RequestToDBObject.convert( it.getOAuth2Request() ),
            userAuth: AuthenticationToDBObject.convert( it.userAuthentication )
        ])
    }

    ConverterAdapter DBObjectToOAuth2Authentication = createConverter DBObject, OAuth2Authentication, {
        def src = it.toMap()

        def oauthRequest = (OAuth2Request) DBObjectToOAuth2Request.convert( src.oauthReq as DBObject )
        def userAuth = (Authentication) DBObjectToAuthentication.convert( src.userAuth as DBObject )

        new OAuth2Authentication(oauthRequest, userAuth)
    }

    ConverterAdapter OAuth2RequestToDBObject = createConverter OAuth2Request, DBObject, {
        new BasicDBObject([
            client:      it.clientId,
            scope:       it.scope,
            reqParams:   it.requestParameters,
            resources:   it.resourceIds,
            authorities: authorityListToSet(it.authorities),
            approved:    it.approved,
            redirect:    it.redirectUri,
            respTypes:   it.responseTypes,
            exts:        it.extensions
        ])
    }

    ConverterAdapter DBObjectToOAuth2Request = createConverter DBObject, OAuth2Request, {
        def src = it.toMap()

        new OAuth2Request (
            src.reqParams as Map,
            src.client as String,
            src.authorities?.collect { new SimpleGrantedAuthority(it as String) },
            src.approved as boolean,
            src.scope as Set,
            src.resources as Set,
            src.redirect as String,
            src.respTypes as Set,
            src.exts as Map
        )
    }

    ConverterAdapter AuthenticationToDBObject = createConverter Authentication, DBObject, {

        def user = (User) (it.principal instanceof User ? it.principal : null)
        new BasicDBObject([
            uname:       it.name,
            email:       user?.email,
            authorities: authorityListToSet(it.authorities),
        ])
    }

    ConverterAdapter DBObjectToAuthentication = createConverter DBObject, Authentication, {
        def src = it.toMap()

        def authorities = src.authorities?.collect { new SimpleGrantedAuthority(it as String) }
        def principal = new User (
            username: src.uname as String,
            email: src.email as String,
            authorities: authorities ?: [] as Collection
        )
        new UsernamePasswordAuthenticationToken(principal, null, authorities)
    }

    ConverterAdapter StringToGrantedAuthority = createConverter String, GrantedAuthority, {
        new SimpleGrantedAuthority(it)
    }

    ConverterAdapter GrantedAuthorityToString = createConverter GrantedAuthority, String, {
        it.authority
    }


    List<? extends GenericConverter> ALL_CONVERTERS = CustomConverters.declaredFields
            .collect { Field f -> f.get(null) }
            .findAll { it instanceof ConverterAdapter }
            .asImmutable() as List<ConverterAdapter>
}
