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

import cz.cvut.zuul.oaas.restapi.config.RestContextConfig
import cz.cvut.zuul.oaas.restapi.config.RestControllersConfig
import cz.cvut.zuul.oaas.web.config.WebContextConfig
import cz.cvut.zuul.oaas.web.config.WebControllersConfig
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.embedded.RegistrationBean
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet

@Configuration
@Import([
    EmbeddedServletContainerAutoConfiguration,
    ServerPropertiesAutoConfiguration,
    RootContextConfig])
class Application extends SpringBootServletInitializer {

    /**
     * Configure and run application in an embedded container; used in
     * executable JAR only.
     */
    static void main(String[] args) {
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


    @Bean RegistrationBean webModuleRegistration() {
        new ServletRegistrationBean (
            name:        'web-module',
            urlMappings: ['/*'],
            servlet:     new DispatcherServlet (
                new AnnotationConfigWebApplicationContext().with {
                    register WebContextConfig, WebControllersConfig; it
                }
            )
        )
    }

    @Bean RegistrationBean restModuleRegistration() {
        new ServletRegistrationBean (
            name:        'rest-module',
            urlMappings: ['/api/*'],
            servlet:     new DispatcherServlet (
                new AnnotationConfigWebApplicationContext().with {
                    register RestContextConfig, RestControllersConfig; it
                }
            ),
        )
    }
}
