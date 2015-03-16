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
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService

import static org.springframework.security.oauth2.common.exceptions.OAuth2Exception.INVALID_CLIENT

/**
 * This is implementation of {@link ClientDetailsService} interface that
 * basically delegates calls to {@link cz.cvut.zuul.oaas.repos.ClientsRepo}.
 */
@Slf4j
class ClientDetailsServiceImpl implements ClientDetailsService {

    final ClientsRepo clientsRepo


    ClientDetailsServiceImpl(ClientsRepo clientsRepo) {
        this.clientsRepo = clientsRepo
    }

    ClientDetails loadClientByClientId(String clientId) {
        log.debug 'Loading client: [{}]', clientId

        def result = clientsRepo.findOne(clientId)

        if (!result) {
            throw OAuth2Exception.create(INVALID_CLIENT, "No such client found with id = ${clientId}")
        }
        result
    }
}
