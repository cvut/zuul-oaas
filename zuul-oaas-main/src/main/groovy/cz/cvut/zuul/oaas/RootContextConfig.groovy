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
package cz.cvut.zuul.oaas

import cz.cvut.zuul.oaas.persistence.config.JdbcPersistenceConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.context.annotation.Import
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity

/**
 * Spring configuration of the root context.
 */
@Configuration
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
@Import([JdbcPersistenceConfig, SecurityRootContextConfig])
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
