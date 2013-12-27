package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

import javax.inject.Inject

abstract class AbstractAuthenticationManagerConfig extends ConfigurationSupport {

    @Inject ObjectPostProcessor<Object> objectPostProcessor

    def getBuilder() {
        new AuthenticationManagerBuilder(objectPostProcessor)
    }
}
