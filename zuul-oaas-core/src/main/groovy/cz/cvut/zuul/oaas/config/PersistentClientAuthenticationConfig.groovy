package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.oauth2.LockableClientUserDetailsService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager

import javax.inject.Inject

@Configuration
class PersistentClientAuthenticationConfig extends AbstractAuthenticationManagerConfig implements ClientAuthenticationBeans {

    @Inject PersistenceBeans repos

    @Bean @Qualifier('client')
    AuthenticationManager clientAuthenticationManager() {
        builder.userDetailsService (
            new LockableClientUserDetailsService (
                clientsRepo: repos.clientsRepo()
            )
        ).and().build()
    }
}
