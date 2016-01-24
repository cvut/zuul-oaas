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
import groovy.util.logging.Slf4j
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.saml.SAMLCredential
import org.springframework.security.saml.userdetails.SAMLUserDetailsService

/**
 * Simple implementation of {@link SAMLUserDetailsService} that populates
 * {@link User} object from SAML attributes.
 */
@Slf4j
class SamlAttributesUserDetailsService implements SAMLUserDetailsService {

    /**
     * Names of SAML attributes that contain the user's e-mail address.
     * If multiple attributes are provided, then the first attribute found in
     * a SAML assertion will be used. Default is {@link Saml2Attributes#MAIL}.
     */
    List<String> emailAttrNames = [ Saml2Attributes.MAIL ]

    /**
     * Names of SAML attributes that contain the user's first name (given name).
     * If multiple attributes are provided, then the first attribute found in
     * a SAML assertion will be used. Default is <tt>urn:oid:2.5.4.42</tt>.
     */
    List<String> firstNameAttrNames = [ Saml2Attributes.GIVEN_NAME ]

    /**
     * Names of SAML attributes that contain the user's last name (surname).
     * If multiple attributes are provided, then the first attribute found in
     * a SAML assertion will be used. Default is <tt>urn:oid:2.5.4.4</tt>.
     */
    List<String> lastNameAttrNames = [ Saml2Attributes.SURNAME ]

    /**
     * Names of SAML attributes that contain the user's username (uid).
     * If multiple attributes are provided, then the first attribute found in
     * a SAML assertion will be used.
     * Default is <tt>urn:oid:0.9.2342.19200300.100.1.1</tt>.
     */
    List<String> usernameAttrNames = [ Saml2Attributes.UID ]

    /**
     * Default authorities that will by granted to any successfully
     * authenticated user. Default is <tt>ROLE_USER</tt>
     */
    List<GrantedAuthority> defaultAuthorities = [new SimpleGrantedAuthority('ROLE_USER')]


    def loadUserBySAML(SAMLCredential credential) {

        def findAttributeValue = { attrNames ->
            attrNames.findResult(credential.&getAttributeAsString)
        }

        def username = findAttributeValue(usernameAttrNames)
        if (!username) {
            throw new UsernameNotFoundException('Username not found in SAML response')
        }

        log.debug "Authenticated user ${credential.nameID.value} with username ${username}"

        new User (
            username: username,
            email: findAttributeValue(emailAttrNames),
            firstName: findAttributeValue(firstNameAttrNames),
            lastName: findAttributeValue(lastNameAttrNames),
            authorities: defaultAuthorities
        )
    }
}
