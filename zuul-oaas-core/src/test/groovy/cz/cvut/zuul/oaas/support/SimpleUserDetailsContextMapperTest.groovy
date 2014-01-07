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
package cz.cvut.zuul.oaas.support

import cz.cvut.zuul.oaas.models.User
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.ldap.core.DirContextOperations
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList

@Mixin(CoreObjectFactory)
class SimpleUserDetailsContextMapperTest extends Specification {

    def dirContext = Mock(DirContextOperations)
    def mapper = new SimpleUserDetailsContextMapper()


    def 'maps LDAP entry to user when default attribute names'() {
        setup:
            dirContext.getStringAttribute('mail') >> expected.email
            dirContext.getStringAttribute('givenName') >> expected.firstName
            dirContext.getStringAttribute('sn') >> expected.lastName
        when:
            def actual = mapper.mapUserFromContext(dirContext, expected.username, expected.authorities)
        then:
            actual instanceof User
            assertThat( actual ).equalsTo( expected ).inProperties('username', 'email', 'firstName', 'lastName')
            actual.authorities as Set == expected.authorities as Set
        where:
            expected << [ build(User), build(User, [authorities: []]) ]
    }

    def 'maps LDAP entry to user when customized attribute names'() {
        setup:
            mapper.emailAttrName = 'preferredEmail'
            mapper.firstNameAttrName = 'givenName;lang-cs'
            mapper.lastNameAttrName = 'sn;lang-cs'

            dirContext.getStringAttribute('preferredEmail') >> expected.email
            dirContext.getStringAttribute('givenName;lang-cs') >> expected.firstName
            dirContext.getStringAttribute('sn;lang-cs') >> expected.lastName
        when:
            def actual = mapper.mapUserFromContext(dirContext, expected.username, expected.authorities)
        then:
            assertThat( actual ).equalsTo( expected ).inProperties('email', 'firstName', 'lastName')
        where:
            expected = build(User)
    }

    def 'merges default roles and LDAP roles'() {
        setup:
            mapper.defaultRoles = defaultAuthorities*.authority
        when:
            def actual = mapper.mapUserFromContext(dirContext, 'flynn', ldapAuthorities)
        then:
            actual.authorities.containsAll( defaultAuthorities + ldapAuthorities )
        where:
            defaultAuthorities = createAuthorityList('ROLE_USER', 'ROLE_ADMIN')
            ldapAuthorities = createAuthorityList('employee', 'geek')
    }
}
