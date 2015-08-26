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
package cz.cvut.zuul.oaas.api.models

import com.fasterxml.jackson.databind.util.ISO8601Utils
import cz.cvut.zuul.oaas.api.test.ApiObjectFactory
import cz.cvut.zuul.oaas.api.support.JsonMapperFactory
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

@Mixin(ApiObjectFactory)
class TokenDTOTest extends Specification {

    @Shared mapper = JsonMapperFactory.getInstance()


    def 'should marshall to JSON'() {
        when:
            def output = mapper.writeValueAsString(token)
        then:
            with (new JsonSlurper().parseText(output)) {
                expiration  == (token.expiration ? ISO8601Utils.format(token.expiration) : null)
                scope       == token.scope as List
                token_type  == token.tokenType
                token_value == token.tokenValue

                with (client_authentication) {
                    client_id     == clientAuth.clientId
                    client_locked == clientAuth.clientLocked
                    display_name  == clientAuth.displayName
                    scope         == clientAuth.scope as List
                    redirect_uri  == clientAuth.redirectUri
                    resource_ids  == clientAuth.resourceIds as List
                }

                with (user_authentication) {
                    username      == userAuth.username
                    email         == userAuth.email
                    first_name    == userAuth.firstName
                    last_name     == userAuth.lastName
                }
            }
        where:
            token = build(TokenDTO).with {
                userAuthentication = build(TokenDTO.UserAuthentication)
                clientAuthentication = build(TokenDTO.ClientAuthentication)
                return it
            }
            userAuth = token.userAuthentication
            clientAuth = token.clientAuthentication
    }
}
