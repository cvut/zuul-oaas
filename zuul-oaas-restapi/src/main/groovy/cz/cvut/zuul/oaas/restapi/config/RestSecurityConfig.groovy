package cz.cvut.zuul.oaas.restapi.config

import cz.cvut.zuul.oarp.spring.config.StandaloneResourceServerConfigurer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

import javax.inject.Inject

import static org.springframework.http.HttpMethod.GET

/**
 * This configuration must be loaded in the root context!
 */
@Configuration
@EnableWebSecurity @Order(2)
class RestSecurityConfig extends WebSecurityConfigurerAdapter {

    // external services
    @Inject @Qualifier('client')
    AuthenticationManager clientAuthenticationManager

    @Inject
    ResourceServerTokenServices tokenServices


    AuthenticationManager authenticationManager() {
        clientAuthenticationManager
    }

    void configure(HttpSecurity http) {
        http.antMatcher('/api/v1/**')
            .apply((SecurityConfigurerAdapter) new StandaloneResourceServerConfigurer())
                .resourceTokenServices(tokenServices)
                .and()
            .authorizeRequests()
                .antMatchers(GET, '/api/v1/resources/**')
                    .permitAll()
                .antMatchers('/api/v1/check-token')
                    .access('#oauth2.hasScope("urn:ctu:oauth:oaas:check-token") and #oauth2.isClient()')
                .antMatchers('/api/v1/**')
                    .access('#oauth2.hasScope("urn:ctu:oauth:oaas:manager") and #oauth2.isClient()')
    }
}
