package cz.cvut.zuul.oaas.web.config

import cz.cvut.zuul.oaas.api.services.ClientsService
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.web.controllers.AccessConfirmationController
import cz.cvut.zuul.oaas.web.controllers.MainController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.inject.Inject

@Configuration
class WebControllersConfig extends ConfigurationSupport {

    // external service
    @Inject ClientsService clientsService

    /**
     * Static pages
     */
    @Bean MainController mainController() {
        new MainController()
    }

    /**
     * Overrides the default mappings for approval and error pages.
     */
    @Bean AccessConfirmationController confirmationController() {
        new AccessConfirmationController (
            clientsService: clientsService
        )
    }
}
