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
package cz.cvut.zuul.oaas.web.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.thymeleaf.spring4.SpringTemplateEngine
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring4.view.ThymeleafViewResolver

@Configuration
@EnableWebMvc
@Import(WebControllersConfig)
class WebContextConfig extends WebMvcConfigurerAdapter implements ConfigurationSupport {

    /**
     * {@code BeanFactoryPostProcessor} that inherits existing property
     * placeholder resolver from the parent context in this context.
     */
    @Bean static BeanFactoryPostProcessor parentPlaceholderConfigurerPostProcessor() {
        new BeanFactoryPostProcessor() {
            void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                beanFactory.getBean(PlaceholderConfigurerSupport).postProcessBeanFactory(beanFactory)
            }
        }
    }

    void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable()
    }

    void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler('/css/**')
                .addResourceLocations('classpath:/static/css/')
        registry.addResourceHandler('/images/**')
                .addResourceLocations('classpath:/static/images/')
    }

    @Bean thymeleafViewResolver() {
        new ThymeleafViewResolver (
            templateEngine:     templateEngine(),
            contentType:        'text/html;charset=UTF-8'
        )
    }

    @Bean templateEngine() {
        new SpringTemplateEngine (
            templateResolver:   templateResolver(),
            messageSource:      messageSource()
        )
    }

    @Bean templateResolver() {
        new SpringResourceTemplateResolver (
            prefix:             'classpath:/templates/',
            suffix:             '.html',
            characterEncoding:  'utf-8',
            templateMode:       'HTML5',
            cacheable:          ! profileDev
        )
    }

    @Bean messageSource() {
        new ReloadableResourceBundleMessageSource (
            basename:           'classpath:/config/web/i18n/web-messages',
            defaultEncoding:    'utf-8',
            cacheSeconds:       profileDev ? 5 : 120
        )
    }
}
