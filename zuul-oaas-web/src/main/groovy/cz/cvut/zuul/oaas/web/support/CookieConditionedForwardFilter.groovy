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

import groovy.util.logging.Slf4j
import org.springframework.util.Assert
import org.springframework.web.filter.GenericFilterBean
import org.springframework.web.util.WebUtils

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Slf4j
class CookieConditionedForwardFilter extends GenericFilterBean {

    String filterProcessesUrl
    String cookieName
    String forwardPath


    @Override void initFilterBean() {

        Assert.hasText(filterProcessesUrl, 'filterProcessesUrl must not be blank')
        Assert.hasText(cookieName, 'cookieName must not be blank')
        Assert.hasText(forwardPath, 'forwardPath must not be blank')
    }

    @Override void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        if (shouldProcessFilter((HttpServletRequest) request)) {
            log.debug "Cookie '${cookieName}' is set, forwarding request from {} to ${forwardPath}",
                ((HttpServletRequest) request).requestURI

            request.getRequestDispatcher(forwardPath).forward(request, response)

        } else {
            chain.doFilter(request, response)
        }
    }

    protected shouldProcessFilter(HttpServletRequest request) {
        request.requestURI.startsWith(filterProcessesUrl) &&
            WebUtils.getCookie(request, cookieName)?.value?.toBoolean()
    }
}
