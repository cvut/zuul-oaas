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
package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.support.SimpleUserDetailsContextMapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator

@Configuration
@Profile('ldap')
class LdapUserAuthenticationConfig extends AbstractAuthenticationManagerConfig {

    @Bean @Qualifier('user')
    AuthenticationManager userAuthManager() {
        builder.ldapAuthentication()
            .contextSource()
                .url( p('auth.user.ldap[0].server.uri') +'/'+ p('auth.user.ldap[0].server.base_dn') )
                .and()
            .ldapAuthoritiesPopulator(new NullLdapAuthoritiesPopulator())
            .userDnPatterns( p('auth.user.ldap[0].user_dn_pattern') )
            .userSearchBase( p('auth.user.ldap[0].user_search_base') )
            .userSearchFilter( p('auth.user.ldap[0].user_search_filter') )
            .userDetailsContextMapper(new SimpleUserDetailsContextMapper (
                firstNameAttrName: p('auth.user.ldap[0].attribute.fist_name'),
                lastNameAttrName: p('auth.user.ldap[0].attribute.last_name'),
                emailAttrName: p('auth.user.ldap[0].attribute.email'),
                defaultRoles: ['ROLE_USER']
            ))

        if (env.containsProperty('auth.user.ldap[1].server.uri')) {
            builder.ldapAuthentication()
                .contextSource()
                    .url( p('auth.user.ldap[1].server.uri') +'/'+ p('auth.user.ldap[1].server.base_dn') )
                    .and()
                .ldapAuthoritiesPopulator(new NullLdapAuthoritiesPopulator())
                .userDnPatterns( p('auth.user.ldap[1].user_dn_pattern') )
                .userSearchBase( p('auth.user.ldap[1].user_search_base') )
                .userSearchFilter( p('auth.user.ldap[1].user_search_filter') )
                .userDetailsContextMapper(new SimpleUserDetailsContextMapper (
                    firstNameAttrName: p('auth.user.ldap[1].attribute.fist_name'),
                    lastNameAttrName: p('auth.user.ldap[1].attribute.last_name'),
                    emailAttrName: p('auth.user.ldap[1].attribute.email'),
                    defaultRoles: ['ROLE_USER']
                ))
        }

        builder.build()
    }
}
