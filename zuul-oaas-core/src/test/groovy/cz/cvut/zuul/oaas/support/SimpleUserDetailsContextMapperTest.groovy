package cz.cvut.zuul.oaas.support

import cz.cvut.zuul.oaas.models.User
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import org.springframework.ldap.core.DirContextOperations
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Mixin(ObjectFactory)
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
