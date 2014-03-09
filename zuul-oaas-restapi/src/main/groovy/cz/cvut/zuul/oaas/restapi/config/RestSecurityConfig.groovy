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
package cz.cvut.zuul.oaas.restapi.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.support.spring.provider.OAuth2ResourceServerConfigurerAdapter
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

import javax.inject.Inject

import static org.springframework.http.HttpMethod.GET

/**
 * This configuration must be loaded in the root context!
 */
@Configuration
@EnableWebSecurity @Order(1)
@Mixin(ConfigurationSupport)
class RestSecurityConfig extends OAuth2ResourceServerConfigurerAdapter {

    // Initialize mixed in ConfigurationSupport
    @Inject initSupport(ApplicationContext ctx) { _initSupport(ctx) }

    // external service
    @Inject ResourceServerTokenServices resourceServerTokenServices


    void configure(HttpSecurity http) {
        http.antMatcher( '/api/v1/**' )
            .authorizeRequests()
                .antMatchers( GET, '/api/v1/resources/public/**' )
                    .permitAll()
                .antMatchers( '/api/v1/tokeninfo' )
                    .access( $('restapi.tokeninfo.security.access') )
                .antMatchers( '/api/v1/**' )
                    .access( $('restapi.security.access') )
    }
}
