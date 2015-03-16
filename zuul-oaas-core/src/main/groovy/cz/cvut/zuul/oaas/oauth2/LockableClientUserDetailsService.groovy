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
package cz.cvut.zuul.oaas.oauth2

import cz.cvut.zuul.oaas.repos.ClientsRepo
import groovy.util.logging.Slf4j
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception

import static org.springframework.security.oauth2.common.exceptions.OAuth2Exception.INVALID_CLIENT

/**
 * Service for populating OAuth2 Client as "UserDetails" that implements
 * {@link UserDetailsService} interface.
 *
 * This replaces the default implementation {@link org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService}
 * for OAuth2 and adds support for lockable Clients.
 */
@Slf4j
class LockableClientUserDetailsService implements UserDetailsService {

    final ClientsRepo clientsRepo
    private String emptyPassword = ''


    LockableClientUserDetailsService(ClientsRepo clientsRepo) {
        this.clientsRepo = clientsRepo
    }

    UserDetails loadUserByUsername(String clientId) {

        def client = clientsRepo.findOne(clientId)

        if (!client) {
            throw OAuth2Exception.create(INVALID_CLIENT, "No such client found with id = ${clientId}")
        }
        if (client.locked) {
            log.info 'Locked client loaded: {}', client
        }

        def clientSecret = client.clientSecret ?: emptyPassword

        new User(clientId, clientSecret, true, true, true, !client.locked, client.authorities)
    }

    /**
     * This is used only to encode empty password which is used when client does
     * not have any password.
     *
     * @param passwordEncoder
     */
    void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.emptyPassword = passwordEncoder.encode('')
    }
}
