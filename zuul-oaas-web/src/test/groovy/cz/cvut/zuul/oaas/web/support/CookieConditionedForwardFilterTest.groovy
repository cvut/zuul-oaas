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
package cz.cvut.zuul.oaas.web.support

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.FilterChain
import javax.servlet.http.Cookie

@Unroll
class CookieConditionedForwardFilterTest extends Specification {

    def filter = new CookieConditionedForwardFilter(
        filterProcessesUrl: '/filtered/path',
        cookieName: 'filter-cookie',
        forwardPath: '/forward/me'
    )

    def passingRequest = new MockHttpServletRequest(
        method: 'get',
        requestURI: '/some/path'
    )
    def matchingRequest = new MockHttpServletRequest(
        method: 'get',
        requestURI: filter.filterProcessesUrl,
        cookies: [new Cookie(filter.cookieName, 'true')]
    )
    def response = new MockHttpServletResponse()
    def filterChain = Mock(FilterChain)


    def 'initFilterBean: throws IllegalArgumentException when #propertyName is not set'() {
        setup:
            def filter = new CookieConditionedForwardFilter()
            (['filterProcessesUrl', 'cookieName', 'forwardPath'] - propertyName).each { name ->
                filter.setProperty(name, 'anything')
            }
        when:
            filter.initFilterBean()
        then:
            thrown IllegalArgumentException
        where:
            propertyName << ['filterProcessesUrl', 'cookieName', 'forwardPath']
    }


    def 'shouldProcessFilter: returns #expected when path #desc filterProcessesUrl and specified cookie is present'() {
        setup:
            def request = new MockHttpServletRequest(
                method: 'get',
                requestURI: path,
                cookies: new Cookie('filter-cookie', 'true')
            )
        expect:
            filter.shouldProcessFilter(request) == expected
        where:
            path                  || expected | desc
            '/any/path'           || false    | "doesn't start with"
            '/foo/filtered/path'  || false    | "doesn't start with"
            '/filtered/path'      || true     | 'matches'
            '/filtered/path.html' || true     | 'starts with'
            '/filtered/path/sub'  || true     | 'starts with'
    }

    def 'shouldProcessFilter: returns #expected when #desc and path starts with filterProcessesUrl'() {
        setup:
            def request = new MockHttpServletRequest(
                method: 'get',
                requestURI: filter.filterProcessesUrl,
                cookies: new Cookie(*cookie.split('='))
            )
        expect:
            filter.shouldProcessFilter(request) == expected
        where:
            cookie                || expected | desc
            'foo=bar'             || false    | 'specified cookie is not found'
            'filter-cookie=false' || false    | 'specified cookie is found with value false'
            'filter-cookie=true'  || true     | 'specified cookie is found with value true'
            'filter-cookie=1'     || true     | 'specified cookie is found with value 1'
    }


    def 'doFilter: do nothing and call next filter when given non-matching request'() {
        when:
            filter.doFilter(passingRequest, response, filterChain)
        then:
            1 * filterChain.doFilter(passingRequest, response)
            response.forwardedUrl == null
    }

    def 'doFilter: forwards request the forwardPath when given matching request'() {
        when:
            filter.doFilter(matchingRequest, response, filterChain)
        then:
           response.forwardedUrl == filter.forwardPath
    }
}
