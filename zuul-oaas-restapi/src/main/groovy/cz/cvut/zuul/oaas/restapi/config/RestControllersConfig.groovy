package cz.cvut.zuul.oaas.restapi.config

import cz.cvut.zuul.oaas.api.services.ClientsService
import cz.cvut.zuul.oaas.api.services.ResourcesService
import cz.cvut.zuul.oaas.api.services.TokensService
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.restapi.controllers.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.inject.Inject

@Configuration
class RestControllersConfig extends ConfigurationSupport {

    // external services
    @Inject ClientsService clientsService
    @Inject ResourcesService resourcesService
    @Inject TokensService tokensService


    @Bean ClientsController clientsController() {
        new ClientsController (
            clientsService: clientsService
        )
    }

    @Bean ResourcesController resourcesController() {
        new ResourcesController (
            resourceService: resourcesService
        )
    }

    @Bean TokensController tokensController() {
        new TokensController (
            tokensService: tokensService
        )
    }

    @Bean CheckTokenEndpoint checkTokenController() {
        new CheckTokenEndpoint (
            tokensService: tokensService
        )
    }

    @Bean CommonExceptionHandler exceptionHandler() {
        new CommonExceptionHandler()
    }
}
