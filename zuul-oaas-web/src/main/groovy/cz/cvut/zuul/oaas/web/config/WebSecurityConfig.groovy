package cz.cvut.zuul.oaas.web.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

import javax.inject.Inject

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE

/**
 * This configuration must be loaded in the root context!
 */
@Configuration
@EnableWebSecurity @Order(LOWEST_PRECEDENCE)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // external service
    @Inject @Qualifier('user')
    AuthenticationManager userAuthenticationManager


    AuthenticationManager authenticationManager() {
        userAuthenticationManager
    }

    void configure(HttpSecurity http) {
        http.antMatcher('/**')
            .csrf()
                .disable()  //TODO enable?
            .exceptionHandling()
                .accessDeniedPage('/login.html?authorization_error=true')
                .and()
            .anonymous()
                .and()
            .formLogin()
                .loginPage('/login.html')
                .loginProcessingUrl('/login.do')
                .usernameParameter('j_username')
                .passwordParameter('j_password')
                .defaultSuccessUrl('/')
                .failureUrl('/login.html?authentication_error=true')
                .and()
            .authorizeRequests()
                .antMatchers('/oauth/**').fullyAuthenticated()
    }
}

