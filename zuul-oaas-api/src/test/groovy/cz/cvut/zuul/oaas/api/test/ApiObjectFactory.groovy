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
package cz.cvut.zuul.oaas.api.test

import cz.cvut.zuul.oaas.api.models.TokenInfo
import cz.cvut.zuul.oaas.api.models.ResourceDTO
import cz.cvut.zuul.oaas.test.ObjectFactory
import cz.cvut.zuul.oaas.test.ObjectFeeder
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyFixedValue
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString

// TODO refactor me!
class ApiObjectFactory extends ObjectFactory {

    ApiObjectFactory() {

        registerBuilder(ResourceDTO) { values ->
            def resource = new ResourceDTO(
                resourceId: anyLetterString(5, 10),
                baseUrl: 'http://example.org',
                description: anyLetterString(0, 255),
                name: anyLetterString(5, 255),
                version: anyLetterString(0, 255),
                visibility: anyFixedValue('public', 'hidden'),
                auth: new ResourceDTO.Auth(
                    scopes: buildListOf(ResourceDTO.Scope, 0, 3)
                )
            )
            values.each { prop, value ->
                resource[prop] = value
            }
            return resource
        }

        registerBuilder(TokenInfo) { values ->
            def token = new TokenInfo(
                clientAuthorities: buildListOf(GrantedAuthority),
                userAuthorities: buildListOf(GrantedAuthority)
            )
            ObjectFeeder.populate(token)
            values.each { prop, value ->
                token[prop] = value
            }
            return token
        }

        registerBuilder(SimpleGrantedAuthority) {
            new SimpleGrantedAuthority(
                anyFixedValue('ROLE_USER', 'ROLE_CLIENT', 'ROLE_ADMIN', 'ROLE_MASTER')
            )
        }

        registerSuperclass GrantedAuthority, SimpleGrantedAuthority
    }
}
