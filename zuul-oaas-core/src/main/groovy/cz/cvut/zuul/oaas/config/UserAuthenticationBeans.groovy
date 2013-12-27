package cz.cvut.zuul.oaas.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager

interface UserAuthenticationBeans {

    @Bean @Qualifier('user')
    AuthenticationManager userAuthenticationManager()
}
