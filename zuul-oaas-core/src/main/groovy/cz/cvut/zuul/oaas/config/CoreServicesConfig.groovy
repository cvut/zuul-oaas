package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.api.services.ClientsService
import cz.cvut.zuul.oaas.api.services.ResourcesService
import cz.cvut.zuul.oaas.api.services.TokensService
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.config.PersistenceBeans
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
class CoreServicesConfig extends ConfigurationSupport {

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
     */
    @Bean methodValidationPostProcessor() {
        new MethodValidationPostProcessor (
            validator:          validator()
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
