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

import groovy.util.logging.Slf4j
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.saml.SAMLCredential
import org.springframework.security.saml.userdetails.SAMLUserDetailsService
import org.springframework.stereotype.Service

// TODO this is just example
@Slf4j
@Service
class SamlUserDetailsServiceImpl implements SAMLUserDetailsService {

    def loadUserBySAML(SAMLCredential credential) {

        // The method is supposed to identify local account of user referenced by
        // data in the SAML assertion and return UserDetails object describing the user.

        def userID = credential.nameID.value

        log.info("${userID} is logged in")

        def authorities = [new SimpleGrantedAuthority('ROLE_USER')]

        // In a real scenario, this implementation has to locate user in a arbitrary
        // dataStore based on information present in the SAMLCredential and
        // returns such a date in a form of application specific UserDetails object.
        new User(userID, '***', true, true, true, true, authorities)
    }
}
