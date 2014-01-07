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
package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.support.SimpleUserDetailsContextMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager

@Configuration
@Profile('ldap')
class LdapUserAuthenticationConfig extends AbstractAuthenticationManagerConfig implements UserAuthenticationBeans {

    @Bean @Qualifier('user')
    AuthenticationManager userAuthenticationManager() {
        builder.ldapAuthentication().with {
            contextSource().url         $('auth.user.ldap.server.uri}') +'/'+ $('auth.user.ldap.server.base_dn')
            userDnPatterns              $('auth.user.ldap.user_dn_pattern')
            userSearchBase              $('auth.user.ldap.user_search_base')
            userSearchFilter            $('auth.user.ldap.user_search_filter')
            userDetailsContextMapper    ldapUserContextMapper()
        }.and().build()
    }

    @Bean ldapUserContextMapper() {
        new SimpleUserDetailsContextMapper (
            firstNameAttrName:  $('auth.user.ldap.attribute.fist_name'),
            lastNameAttrName:   $('auth.user.ldap.attribute.last_name'),
            emailAttrName:      $('auth.user.ldap.attribute.email'),
            defaultRoles:       'ROLE_USER'
        )
    }
}
