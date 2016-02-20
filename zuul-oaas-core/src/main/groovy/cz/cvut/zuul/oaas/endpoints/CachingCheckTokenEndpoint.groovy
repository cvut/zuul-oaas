/*
 * The MIT License
 *
 * Copyright 2013-2016 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.endpoints

import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.cache2k.Cache
import org.cache2k.CacheBuilder
import org.cache2k.CacheEntry
import org.cache2k.CacheSource
import org.cache2k.EntryExpiryCalculator
import org.cache2k.PropagatedCacheException
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

/**
 * This class is a replacement for {@link org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint}.
 */
@Slf4j
@CompileStatic
@FrameworkEndpoint
class CachingCheckTokenEndpoint {

    private final AccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter()

    private final Cache<String, Map> cache

    final AccessTokensRepo accessTokensRepo

    WebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator()


    CachingCheckTokenEndpoint(AccessTokensRepo accessTokensRepo, CacheBuilder<String, Map> cacheBuilder) {
        this.accessTokensRepo = accessTokensRepo

        def expiryCalculator = { String key, Map value, long fetchTime, CacheEntry entry ->
            (value[AccessTokenConverter.EXP] as long) * 1000  // convert to milliseconds
        }
        this.cache = cacheBuilder
            .suppressExceptions(false)
            .source(this.&readAccessToken as CacheSource)
            .entryExpiryCalculator(expiryCalculator as EntryExpiryCalculator)
            .build()
    }


    @ResponseBody
    @RequestMapping('/oauth/check_token')
    def checkToken(@RequestParam('token') String value) {
        try {
            cache.get(value)
        } catch (PropagatedCacheException ex) {
            throw ex.cause
        }
    }

    @ExceptionHandler
    def handleException(InvalidTokenException ex) {
        log.info "Handling error: ${ex.class.simpleName}, ${ex.message}"

        // This isn't an oauth resource, so we don't want to send an
        // unauthorized code here. The client has already authenticated
        // successfully with basic auth and should just get back the invalid
        // token error.
        exceptionTranslator.translate(new InvalidTokenException(ex.message) {
            int getHttpErrorCode() { 400 }
        })
    }

    private readAccessToken(String tokenValue) {
        def token = accessTokensRepo.findOne(tokenValue)

        if (token == null) {
            throw new InvalidTokenException('Token was not recognised')
        }
        if (token.expired) {
            throw new InvalidTokenException('Token has expired')
        }
        accessTokenConverter.convertAccessToken(token, token.authentication)
    }
}
