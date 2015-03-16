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
package cz.cvut.zuul.oaas.support

import cz.cvut.zuul.oaas.models.User
import groovy.util.logging.Slf4j
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper

/**
 * The context mapper used by the LDAP authentication provider to create an
 * LDAP user object of type {@link User}.
 */
@Slf4j
class SimpleUserDetailsContextMapper implements UserDetailsContextMapper {

    /**
     * The name of the LDAP attribute which contains the user's e-mail address.
     * Defaults to 'mail'.
     */
    String emailAttrName = 'mail'

    /**
     * The name of the LDAP attribute which contains the user's first name.
     * Defaults to 'givenName'.
     */
    String firstNameAttrName = 'givenName'

    /**
     * The name of the LDAP attribute which contains the user's last name.
     * Defaults to 'sn'.
     */
    String lastNameAttrName = 'sn'

    /**
     * The default authorities that will by granted to any successfully
     * authenticated user.
     */
    List<GrantedAuthority> defaultAuthorities = []

    /**
     * The default roles that will by granted to any successfully authenticated
     * user. These will be converted to {@link GrantedAuthority}s and added to
     * set of authorities in the returned LdapUserDetails object.
     */
    void setDefaultRoles(Set<String> defaultRoles) {
        defaultAuthorities = defaultRoles.collect { new SimpleGrantedAuthority(it) }
    }


    UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                   Collection<? extends GrantedAuthority> authorities) {

        log.debug 'Mapping user entry: {}', ctx.nameInNamespace

        def email = ctx.getStringAttribute(emailAttrName)
        def firstName = ctx.getStringAttribute(firstNameAttrName)
        def lastName = ctx.getStringAttribute(lastNameAttrName)
        def mergedAuthorities = (authorities + defaultAuthorities) as LinkedHashSet

        new User(username, email, firstName, lastName, mergedAuthorities)
    }

    void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException('This class supports only reading from a context.')
    }
}
