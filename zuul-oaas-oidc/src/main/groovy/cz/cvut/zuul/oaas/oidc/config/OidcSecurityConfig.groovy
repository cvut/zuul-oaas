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
package cz.cvut.zuul.oaas.oidc.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

@Configuration
@EnableWebSecurity @Order(20)
class OidcSecurityConfig implements ResourceServerConfigurer {

    // external service
    @Autowired ResourceServerTokenServices resourceServerTokenServices


    void configure(ResourceServerSecurityConfigurer resources) {
        resources
            .resourceId(null)  // do not require specific resourceId in token
            .tokenServices(resourceServerTokenServices)
    }

    void configure(HttpSecurity http) {
        http.antMatcher( '/oauth/userinfo' )
            .authorizeRequests()
                .antMatchers( '/oauth/userinfo' )
                    .access( '#oauth2.isUser()' )
    }
}
