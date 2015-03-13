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
package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.api.services.ClientsService
import cz.cvut.zuul.oaas.api.services.ResourcesService
import cz.cvut.zuul.oaas.api.services.TokensService
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.services.ClientsServiceImpl
import cz.cvut.zuul.oaas.services.ResourcesServiceImpl
import cz.cvut.zuul.oaas.services.TokensServiceImpl
import cz.cvut.zuul.oaas.services.generators.RandomizedIdentifierEncoder
import cz.cvut.zuul.oaas.services.generators.SecurePasswordGenerator
import cz.cvut.zuul.oaas.services.generators.UUIDStringGenerator
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator
import org.hibernate.validator.messageinterpolation.ValueFormatterMessageInterpolator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor

import javax.inject.Inject

@Configuration
class CoreServicesConfig implements ConfigurationSupport {

    @Inject PersistenceBeans repos


    @Bean ResourcesService resourcesService() {
        new ResourcesServiceImpl (
            resourcesRepo:      repos.resourcesRepo(),
            identifierEncoder:  identifierEncoder()
        )
    }

    @Bean ClientsService clientsService() {
        new ClientsServiceImpl (
            clientsRepo:        repos.clientsRepo(),
            accessTokensRepo:   repos.accessTokensRepo(),
            refreshTokensRepo:  repos.refreshTokensRepo(),
            clientIdGenerator:  clientIdGenerator(),
            secretGenerator:    secretGenerator()
        )
    }

    @Bean TokensService tokensService() {
        new TokensServiceImpl (
            accessTokensRepo:   repos.accessTokensRepo(),
            clientsRepo:        repos.clientsRepo()
        )
    }

    /**
     * Enables method-level validation on annotated methods via JSR-303.
     * The return value must be typed!
     */
    @Bean MethodValidationPostProcessor methodValidationPostProcessor() {
        new MethodValidationPostProcessor (
            validator: validator()
        )
    }

    @Bean @Primary
    LocalValidatorFactoryBean validator() {
        def interpolator = new ValueFormatterMessageInterpolator (
            new ResourceBundleMessageInterpolator (
                new MessageSourceResourceBundleLocator( validatorMessageSource() )
            )
        )
        new LocalValidatorFactoryBean (
            messageInterpolator: interpolator
        )
    }

    @Bean validatorMessageSource() {
        new ReloadableResourceBundleMessageSource (
            basename:           'classpath:/config/i18n/validator-messages',
            defaultEncoding:    'utf-8',
            cacheSeconds:       profileDev ? 5 : 0
        )
    }


    @Bean secretGenerator() {
        new SecurePasswordGenerator()
    }

    @Bean clientIdGenerator() {
        new UUIDStringGenerator()
    }

    @Bean identifierEncoder() {
        new RandomizedIdentifierEncoder()
    }
}
