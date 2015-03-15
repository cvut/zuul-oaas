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
package cz.cvut.zuul.oaas.restapi.controllers

import cz.cvut.zuul.oaas.api.services.TokensService
import cz.jirutka.spring.exhandler.messages.ErrorMessage
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.web.bind.annotation.*

import static java.lang.Math.min
import static org.springframework.http.HttpStatus.CONFLICT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.web.bind.annotation.RequestMethod.GET

@RestController
@RequestMapping('/v1/tokeninfo')
class TokenInfoController {

    TokensService tokensService

    int cacheMaxAge = 120


    @RequestMapping(method = GET)
    def getTokenInfo(@RequestParam String token) {

        def body = tokensService.getTokenInfo(token)
        def maxAge = min(body.expiresIn, cacheMaxAge)
        def headers = new HttpHeaders(cacheControl: "private,max-age=${maxAge}")

        new ResponseEntity<>(body, headers, OK)
    }


    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    def handleInvalidTokenException(InvalidTokenException ex) {
        new ErrorMessage (
            title:  'Invalid Token',
            status: CONFLICT.value(),
            detail: ex.localizedMessage
        )
    }
}
