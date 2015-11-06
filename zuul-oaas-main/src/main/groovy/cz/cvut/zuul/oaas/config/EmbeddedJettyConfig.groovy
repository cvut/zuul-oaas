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
package cz.cvut.zuul.oaas.config

import org.eclipse.jetty.server.ForwardedRequestCustomizer
import org.eclipse.jetty.server.HttpConfiguration.ConnectionFactory
import org.eclipse.jetty.server.Server
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizerBeanPostProcessor
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties([ServerProperties])
class EmbeddedJettyConfig {


    @Bean static BeanPostProcessor servletContainerCustomizerBeanPostProcessor() {
        new EmbeddedServletContainerCustomizerBeanPostProcessor()
    }

    @Bean JettyEmbeddedServletContainerFactory jettyEmbeddedFactory() {

        def proxyHeadersCustomizer = { Server srv ->
            def customizer = new ForwardedRequestCustomizer()
            findHttpConfigurations(srv).each { it.addCustomizer(customizer) }
        }

        new JettyEmbeddedServletContainerFactory (
            serverCustomizers: [ proxyHeadersCustomizer as JettyServerCustomizer ]
        )
    }


    def findHttpConfigurations(Server server) {
        server.connectors
            .collectMany { it.connectionFactories }
            .findAll { it instanceof ConnectionFactory }
            .collect { ((ConnectionFactory) it).httpConfiguration }
    }
}
