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
package cz.cvut.zuul.oaas.api.models

import cz.cvut.zuul.oaas.api.support.JsonMapperFactory
import cz.cvut.zuul.oaas.api.test.ApiObjectFactory
import cz.cvut.zuul.oaas.api.test.ValidatorUtils
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static cz.cvut.zuul.oaas.api.test.ValidatorUtils.*
import static cz.cvut.zuul.oaas.test.Assertions.assertThat

@Unroll
@Mixin(ApiObjectFactory)
class ClientDTOTest extends Specification {

    @Delegate
    static ValidatorUtils validator = createValidator(ClientDTO.class)

    @Shared mapper = JsonMapperFactory.getInstance()


    void 'should marshall to JSON and vice versa'() {
        when:
            def output = mapper.writeValueAsString(input)
        then:
            with(new JsonSlurper().parseText(output)) {
                client_id              == input.clientId
                client_secret          == input.clientSecret
                scope                  == input.scope
                resource_ids           == input.resourceIds
                authorized_grant_types == input.authorizedGrantTypes
                redirect_uris          == input.redirectUris
                authorities            == input.authorities
                access_token_validity  == input.accessTokenValidity
                refresh_token_validity == input.refreshTokenValidity
                display_name           == input.displayName
                client_locked          == input.locked
                client_type            == input.clientType
            }
        when:
            def readed = mapper.readValue(output, ClientDTO)
        then:
            assertThat( readed ).equalsTo( input ).inAllProperties()
        where:
            input = build(ClientDTO)
    }


    void 'scope should be #expected given #description scopes'() {
        expect:
            validate 'scope', value, expected
        where:
            description          | value                                             || expected
            'empty'              | []                                                || valid
            'valid plain'        | ['poign', 'chu_ky.B4c-0n']                        || valid
            'valid URI'          | ['urn:sample.read', 'http://cvut.cz/scope#read']  || valid
            'with illegal chars' | ['first-valid', 'spa ce', 'quote\'', 'back\\']    || invalid
            'too short'          | ['o', 'four', 'valid']                            || invalid
            'too long'           | ['c' * 257]                                       || invalid
    }

    void 'authorizedGrantTypes should be #expected given #description grants'() {
        expect:
            validate 'authorizedGrantTypes', value, expected
        where:
            description       | value                                           || expected
            'RFC 6749 grants' | ['client_credentials', 'authorization_code',
                                 'implicit', 'resource_owner', 'refresh_token'] || valid
            'empty'           | []                                              || invalid
            'invalid'         | ['evil-grant']                                  || invalid
    }

    void 'redirectUris should be #expected given #description URI'() {
        expect:
            validate 'redirectUris', value, expected
        where:
            description     | value                                     || expected
            'empty'         | []                                        || valid
            'valid'         | ['http://cvut.cz', 'urn:zuul:oauth']       || valid
            'invalid'       | ['foo', 'baaaar']                         || invalid
            'relative'      | ['/relative/url', '../another/relative']  || invalid
            'with fragment' | ['http://cvut.cz/cool#fragment']          || invalid
            'too long'      | ['http://cvut.cz', 'c' * 242]             || invalid
    }

    void 'should be valid given empty redirect URIs when grant type is not "authorization_code"'() {
        given:
            def client = new ClientDTO(
                    redirectUris: [],
                    authorizedGrantTypes: ['client_credentials']
            )
        expect:
            isValid client
    }

    void 'should be invalid given empty redirect URIs when grant type is "authorization_code"'() {
        given:
            def client = new ClientDTO(
                    redirectUris: redirectUris,
                    authorizedGrantTypes: ['authorization_code']
            )
        expect:
            isInvalid client
        where:
            redirectUris << [[], null]
    }
}
