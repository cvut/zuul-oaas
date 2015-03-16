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
package cz.cvut.zuul.oaas.oauth2;

import cz.cvut.zuul.oaas.repos.ClientsRepo;
import cz.cvut.zuul.oaas.models.Client;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service for populating OAuth2 Client as "UserDetails" that implements
 * {@link UserDetailsService} interface.
 *
 * This replaces the default implementation {@link org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService}
 * for OAuth2 and adds support for lockable Clients.
 */
@Slf4j
public class LockableClientUserDetailsService implements UserDetailsService {

    private @Setter ClientsRepo clientsRepo;
    private String emptyPassword = "";


    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        Client client = clientsRepo.findOne(clientId);

        if (client == null) {
            throw OAuth2Exception.create(OAuth2Exception.INVALID_CLIENT, "No such client found with id = " + clientId);
        }

        String clientSecret = client.getClientSecret();
        if (isBlank(clientSecret)) {
            clientSecret = emptyPassword;
        }

        if (log.isInfoEnabled() && client.isLocked()) {
            log.info("Locked client loaded: {}", client);
        }
        return new User(clientId, clientSecret, true, true, true, !client.isLocked(), client.getAuthorities());
    }


    /**
     * This is used only to encode empty password which is used when client does
     * not have any password.
     *
     * @param passwordEncoder
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.emptyPassword = passwordEncoder.encode("");
    }
}
