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
package cz.cvut.zuul.oaas

import cz.cvut.zuul.oaas.oidc.config.OidcContextConfig
import cz.cvut.zuul.oaas.restapi.config.RestContextConfig
import cz.cvut.zuul.oaas.web.config.WebContextConfig
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.embedded.RegistrationBean
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(RootContextConfig)
class Application extends SpringBootServletInitializer {

    /**
     * Configure and run application in an embedded container; used in
     * executable JAR only.
     */
    static void main(String[] args) {
        // allow to use shorten option for config file
        args = args*.replace('--config=', '--spring.config.location=')

        new SpringApplicationBuilder()
                .sources(Application)
                .showBanner(false)
                .run(args)
    }

    /**
     * Configure application; used in classic WAR only.
     */
    SpringApplicationBuilder configure(SpringApplicationBuilder app) {
        app.sources(Application)
           .showBanner(false)
    }


    @Bean RegistrationBean webModuleServlet() {
        new DispatcherServletRegistrationBean (
            name:          'web-module',
            urlMappings:   ['/*'],
            configClasses: [WebContextConfig]
        )
    }

    @Bean RegistrationBean restModuleServlet() {
        new DispatcherServletRegistrationBean (
            name:          'rest-module',
            urlMappings:   ['/api/*'],
            configClasses: [RestContextConfig]
        )
    }

    @Bean RegistrationBean oidcModuleServlet() {
        new DispatcherServletRegistrationBean(
            name: 'oidc-module',
            urlMappings: ['/oauth/userinfo'],  // FIXME!
            configClasses: [OidcContextConfig]
        )
    }
}
