package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.access.vote.AuthenticatedVoter
import org.springframework.security.access.vote.UnanimousBased
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint
import org.springframework.security.oauth2.provider.vote.ScopeVoter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

import javax.inject.Inject

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS

@Configuration
@EnableWebSecurity @Order(0)
@Mixin(ConfigurationSupport)
class TokenEndpointSecurityConfig extends WebSecurityConfigurerAdapter {

    // Initialize mixed in ConfigurationSupport
    @Inject initSupport(ApplicationContext ctx) { _initSupport(ctx) }

    @Inject ClientAuthenticationBeans clientAuthentication

    TokenEndpointSecurityConfig() {
        super(true) // disable defaults
    }

    AuthenticationManager authenticationManager() {
        clientAuthentication.clientAuthenticationManager()
    }

    /**
     * Client authentication with HTTP Basic scheme. This is the recommended way by specification.
     */
    @Bean oauthAuthenticationEntryPoint() {
        new OAuth2AuthenticationEntryPoint (
            realmName: 'Zuul OAAS'
        )
    }

    @Bean accessDeniedHandler() {
        new OAuth2AccessDeniedHandler()
    }

    /**
     * Filter that supports client authentication with credentials in request body (parameters
     * client_id and client_secret). Note: This method of authentication is not recommended by
     * OAuth specification (draft-ietf-oauth-v2-31, 16)! Clients should use HTTP Basic scheme
     * instead.
     */
    @Bean clientCredentialsTokenEndpointFilter() {
        new ClientCredentialsTokenEndpointFilter (
            filterProcessesUrl: $('oaas.endpoint.token'),
            authenticationManager: authenticationManager()
        )
    }

    //TODO ??
    @Bean accessDecisionManager() {
        new UnanimousBased([ new ScopeVoter(), new AuthenticatedVoter() ])
    }

    void configure(HttpSecurity http) {
        http.antMatcher( $('oaas.endpoint.token') )
            .exceptionHandling()
                .authenticationEntryPoint( oauthAuthenticationEntryPoint() )
                .accessDeniedHandler( accessDeniedHandler() )
                .and()
            .headers()
                .cacheControl()
                .and()
            .sessionManagement()
                .sessionCreationPolicy( STATELESS )
                .and()
            .servletApi()
                .and()
            .httpBasic()
                .authenticationEntryPoint( oauthAuthenticationEntryPoint() )
                .and()
            .authorizeRequests()
                .antMatchers( $('oaas.endpoint.token') ).fullyAuthenticated()
                .and()
            // include this only if you need to authenticate clients via request parameters
            .addFilterBefore( clientCredentialsTokenEndpointFilter(), BasicAuthenticationFilter )
    }
}
