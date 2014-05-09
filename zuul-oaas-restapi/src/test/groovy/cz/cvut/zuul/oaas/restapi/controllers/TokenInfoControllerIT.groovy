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
package cz.cvut.zuul.oaas.restapi.controllers

import cz.cvut.zuul.oaas.api.models.TokenInfo
import cz.cvut.zuul.oaas.api.services.TokensService
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException

import javax.inject.Inject

import static java.lang.Math.min

class TokenInfoControllerIT extends AbstractControllerIT {

    @Inject TokenInfoController controller

    def tokensService = Mock(TokensService)


    def 'GET invalid token info'() {
        setup:
            1 * tokensService.getTokenInfo('123') >> { throw new InvalidTokenException('foo') }
        when:
            perform GET('/?token={value}', '123')
        then:
            response.status == 409
    }

    def 'GET valid token info'() {
        setup:
            1 * tokensService.getTokenInfo('123') >> build(TokenInfo)
        when:
            perform request
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
        where:
            request << [
                GET('/?token=123'),
                GET('/?token=123').header('Authorization', 'Bearer 987'),
                GET('/?token=123&access_token=987')
            ]
    }

    def 'GET token info with correct Cache-Control'() {
        setup:
            controller.cacheMaxAge = 120
        and:
            1 * tokensService.getTokenInfo('123') >> build(TokenInfo, [expiresIn: expiresIn])
        when:
            perform GET('/?token={value}', '123')
        then:
            response.getHeader('Cache-Control') == "private,max-age=${maxAge}"
        where:
            expiresIn << [600, 60]
            maxAge = min(120, expiresIn)
    }
}
