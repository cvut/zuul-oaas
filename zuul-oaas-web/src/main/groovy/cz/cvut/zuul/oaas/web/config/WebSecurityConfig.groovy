package cz.cvut.zuul.oaas.web.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

import javax.inject.Inject

/**
 * This configuration must be loaded in the root context!
 */
@Configuration
@EnableWebSecurity @Order(1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // external service
    @Inject @Qualifier('user')
    AuthenticationManager userAuthenticationManager


    AuthenticationManager authenticationManager() {
        userAuthenticationManager
    }

    void configure(HttpSecurity http) {
        http.formLogin()
                .loginPage('/login.html')
                .loginProcessingUrl('/login.do')
                .defaultSuccessUrl('/')
                .failureUrl('/login.html?authentication_error=true')
                .and()
            .anonymous()
                .and()
            .exceptionHandling()
                .accessDeniedPage('/login.html?authorization_error=true')
                .and()
            .authorizeRequests()
                .antMatchers('/oauth/**').fullyAuthenticated()
    }
}

