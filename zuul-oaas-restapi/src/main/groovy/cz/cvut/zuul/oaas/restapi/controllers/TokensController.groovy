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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.web.bind.annotation.RequestMethod.DELETE
import static org.springframework.web.bind.annotation.RequestMethod.GET

/**
 * API for authorization server tokens' management.
 */
@RestController
@RequestMapping('/v1/tokens')
class TokensController {

    TokensService tokensService


    @RequestMapping(value = '{tokenValue}', method = GET)
    def getTokenDetails(@PathVariable String tokenValue) {
        tokensService.getToken(tokenValue)
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = '{tokenValue}', method = DELETE)
    void invalidateToken(@PathVariable String tokenValue) {
        tokensService.invalidateToken(tokenValue)
    }
}
