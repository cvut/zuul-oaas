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
package cz.cvut.zuul.oaas.saml.sp

import cz.cvut.zuul.oaas.models.User
import org.opensaml.saml2.core.NameID
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.saml.SAMLCredential
import spock.lang.Specification
import spock.lang.Unroll

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

@Unroll
class SamlAttributesUserDetailsServiceTest extends Specification {

    static userFields = [ User.&email, User.&firstName, User.&lastName, User.&username ]

    def service = new SamlAttributesUserDetailsService()
    def credential = Mock(SAMLCredential) {
        it.nameID >> Stub(NameID)
    }
    def user = new User(
        username: 'flynn', email: 'flynn@encom.com', firstName: 'Kevin', lastName: 'Flynn')


    def setup() {
        [   // custom attributes
            mail:      user.email,
            givenName: user.firstName,
            surname:   user.lastName,
            uid:       user.username
        ].each { attrName, value ->
            credential.getAttributeAsString(attrName) >> value
        }
    }


    def 'maps default SAML attributes to User when available and default mapping is used'() {
        setup:
            credential.getAttributeAsString(Saml2Attributes.MAIL) >> user.email
            credential.getAttributeAsString(Saml2Attributes.GIVEN_NAME) >> user.firstName
            credential.getAttributeAsString(Saml2Attributes.SURNAME) >> user.lastName
            credential.getAttributeAsString(Saml2Attributes.UID) >> user.username
        when:
            def actual = service.loadUserBySAML(credential)
        then:
            assertThat( actual ).equalsTo( user ).inAllPropertiesExcept( 'authorities' )
    }

    def "maps correct SAML attribute to User's #fieldName when available and mapping is customized"() {
        setup:
            service = new SamlAttributesUserDetailsService (
                emailAttrNames:     ['mail'],
                firstNameAttrNames: ['givenName'],
                lastNameAttrNames:  ['surname'],
                usernameAttrNames:  ['uid']
            )
        when:
            def actual = service.loadUserBySAML(credential)
        then:
            actual./$fieldName/ == user./$fieldName/
        where:
            fieldName << userFields*.method
    }

    def 'uses the first available attribute when multiple SAML attributes for #fieldName are defined'() {
        setup:
            service = new SamlAttributesUserDetailsService (
                emailAttrNames:     ['foo', 'mail', 'bar'],
                firstNameAttrNames: ['foo', 'givenName', 'bar'],
                lastNameAttrNames:  ['foo', 'surname', 'bar'],
                usernameAttrNames:  ['foo', 'uid', 'bar']
            )
        when:
            def actual = service.loadUserBySAML(credential)
        then:
            actual./$fieldName/ == user./$fieldName/
        where:
            fieldName << userFields*.method
    }

    def 'returns empty User when no mapped SAML attributes are present in SAML assertion'() {
        when:
            def actual = service.loadUserBySAML(credential)
        then:
            assertThat( actual ).equalsTo( new User() ).inAllPropertiesExcept( 'authorities' )
    }


    def 'sets provided defaultAuthorities to the returned User object'() {
        setup:
            def authorities = [new SimpleGrantedAuthority('ROLE_FOO')]
            service = new SamlAttributesUserDetailsService(defaultAuthorities: authorities)
        when:
            def actual = service.loadUserBySAML(credential)
        then:
            actual.authorities as Set == authorities as Set
    }
}
