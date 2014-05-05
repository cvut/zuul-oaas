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

import org.springframework.context.annotation.*
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity

/**
 * Spring configuration of the root context.
 */
@Configuration
/*
 * Register configuration properties for placeholders replacement.
 *
 * The first location points to the bundled properties file with default values.
 * Location of the second file is specified by the environment variable
 * 'zuul.config.file' and when exists, then it overrides the default values.
 * This variable can be set via JNDI (see /META-INF/context.xml) or command
 * line parameter (-Dzuul.config.file).
 */
@PropertySources([
    @PropertySource('classpath:/config/zuul-config.properties'),
    @PropertySource(value='file:${java:comp/env/zuul.config.file}', ignoreResourceNotFound=true)])
/*
 * Export MBeans via JMX for monitoring.
 */
@EnableMBeanExport
/*
 * Find and load all configs inside the core module.
 */
@ComponentScan('cz.cvut.zuul.oaas.config')
/*
 * Find and load security configs (annotated with @EnableWebSecurity) from all
 * modules into the root context. Spring Security cannot be initialized across
 * more contexts. :(
 */
@Import(SecurityRootContextConfig)
class RootContextConfig {

    @Configuration
    @ComponentScan(
        basePackages = 'cz.cvut.zuul.oaas',
        useDefaultFilters = false,
        includeFilters = @Filter([EnableWebSecurity, EnableWebMvcSecurity]))
    static class SecurityRootContextConfig {}


    @Bean static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        new PropertySourcesPlaceholderConfigurer (
            fileEncoding: 'UTF-8'
        )
    }
}
