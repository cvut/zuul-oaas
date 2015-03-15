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

import cz.cvut.zuul.oaas.api.exceptions.ConflictException
import cz.cvut.zuul.oaas.api.exceptions.NotFoundException
import cz.cvut.zuul.oaas.api.support.JsonMapperFactory
import cz.jirutka.spring.exhandler.RestHandlerExceptionResolver
import cz.jirutka.spring.exhandler.handlers.ResponseStatusRestExceptionHandler
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver

import static cz.jirutka.spring.exhandler.support.HttpMessageConverterUtils.getDefaultHttpMessageConverters
import static org.springframework.http.HttpStatus.*
import static org.springframework.http.MediaType.APPLICATION_JSON

@Configuration
@EnableWebMvc
@Import(RestControllersConfig)
class RestContextConfig extends WebMvcConfigurerAdapter {

    /**
     * {@code BeanFactoryPostProcessor} that inherits existing property
     * placeholder resolver from the parent context in this context.
     */
    @Bean static BeanFactoryPostProcessor parentPlaceholderConfigurerPostProcessor() {
        new BeanFactoryPostProcessor() {
            void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                try {
                    beanFactory.getBean(PlaceholderConfigurerSupport).postProcessBeanFactory(beanFactory)
                } catch (NoSuchBeanDefinitionException ex) {
                    // is not available in integration test; TODO solve it better
                }
            }
        }
    }

    void configureMessageConverters(List converters) {
        converters << new MappingJackson2HttpMessageConverter (
            objectMapper: JsonMapperFactory.getInstance()
        )
    }

    void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType( APPLICATION_JSON )
    }

    void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add( exceptionHandlerExceptionResolver() ); // resolves @ExceptionHandler
        resolvers.add( restExceptionResolver() );
    }

    @Bean restExceptionResolver() {
        RestHandlerExceptionResolver.builder()
                .messageSource(new ReloadableResourceBundleMessageSource (
                    basename:        'classpath:/cz/cvut/zuul/oaas/restapi/exceptions',
                    defaultEncoding: 'utf-8'
                ))
                .defaultContentType( APPLICATION_JSON )
                .addErrorMessageHandler( NotFoundException, NOT_FOUND )
                .addErrorMessageHandler( ConflictException, CONFLICT )
                // TODO is this really needed?
                .addHandler( BadClientCredentialsException, new ResponseStatusRestExceptionHandler(UNAUTHORIZED) )
                .build();
    }

    @Bean exceptionHandlerExceptionResolver() {
        new ExceptionHandlerExceptionResolver (
            messageConverters: defaultHttpMessageConverters
        )
    }
}
