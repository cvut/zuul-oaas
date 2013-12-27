package cz.cvut.zuul.oaas.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager

@Configuration
@Profile('dev')
class InMemoryUserAuthenticationConfig extends AbstractAuthenticationManagerConfig implements UserAuthenticationBeans {

    @Bean @Qualifier('user')
    AuthenticationManager userAuthenticationManager() {
        builder.inMemoryAuthentication()
            .withUser('tomy')
                .password('best').authorities('ROLE_USER')
               .and()
            .and()
        .build()
    }
}
