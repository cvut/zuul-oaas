package cz.cvut.zuul.oaas.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager

public interface ClientAuthenticationBeans {

    @Bean @Qualifier('client')
    AuthenticationManager clientAuthenticationManager()

}
