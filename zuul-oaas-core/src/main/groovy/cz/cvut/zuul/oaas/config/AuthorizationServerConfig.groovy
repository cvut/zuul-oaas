package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.oauth2.ClientDetailsServiceImpl
import cz.cvut.zuul.oaas.oauth2.LockableClientUserApprovalHandler
import cz.cvut.zuul.oaas.oauth2.TokenStoreImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.oauth2.provider.CompositeTokenGranter
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequestManager
import org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

import javax.inject.Inject

@Configuration
class AuthorizationServerConfig extends ConfigurationSupport {

    @Inject PersistenceBeans repos
    @Inject ClientAuthenticationBeans clientAuthentication

    @Value('${oaas.refresh_token.enabled}') boolean isRefreshToken
    @Value('${oaas.grant.implicit.enabled}') boolean isImplicitGrant
    @Value('${oaas.grant.authorization_code.enabled}') boolean isAuthCodeGrant

    /**
     * A user approval handler that prevents locked clients to be authorized.
     * This is important especially for clients with implicit grant which are
     * issued an access token directly without requesting token endpoint.
     */
    @Bean clientApprovalHandler() {
        new LockableClientUserApprovalHandler (
            clientsRepo:   repos.clientsRepo(),
            parentHandler: new TokenServicesUserApprovalHandler (
                tokenServices: tokenServices()
            )
        )
    }

    @Bean tokenServices() {
        new DefaultTokenServices (
            supportRefreshToken:         isRefreshToken,
            accessTokenValiditySeconds:  $('oaas.access_token.validity', int),
            refreshTokenValiditySeconds: $('oaas.refresh_token.validity', int),
            clientDetailsService:        clientDetailsService(),
            tokenStore:                  tokenStore()
        )
    }

    // Register explicitly under this interface so it can be autowired by this type.
    @Bean ResourceServerTokenServices resourceServerTokenServices() {
        tokenServices()
    }

    @Bean tokenStore() {
        new TokenStoreImpl (
            accessTokensRepo:  repos.accessTokensRepo(),
            refreshTokensRepo: repos.refreshTokensRepo()
        )
    }

    @Bean clientDetailsService() {
        new ClientDetailsServiceImpl (
            clientsRepo: repos.clientsRepo()
        )
    }

    @Bean tokenGranter() {
        def granters = []

        if ( isAuthCodeGrant ) {
            granters << authorizationCodeTokenGranter()
        }
        if ( isRefreshToken ) {
            granters << new RefreshTokenGranter(tokenServices(), clientDetailsService())
        }
        if ( isImplicitGrant ) {
            granters << new ImplicitTokenGranter(tokenServices(), clientDetailsService())
        }
        if ( $('oaas.grant.client_credentials.enabled', boolean) ) {
            granters << new ClientCredentialsTokenGranter(tokenServices(), clientDetailsService())
        }
        if ( $('oaas.grant.password.enabled', boolean) ) {
            granters << new ResourceOwnerPasswordTokenGranter(
                    clientAuthentication.clientAuthenticationManager(), tokenServices(), clientDetailsService())
        }
        new CompositeTokenGranter(granters)
    }

    @Bean @Lazy authorizationCodeTokenGranter() {
        new AuthorizationCodeTokenGranter(tokenServices(), authorizationCodeServices(), clientDetailsService())
    }

    @Bean @Lazy authorizationCodeServices() {
        new InMemoryAuthorizationCodeServices()
    }

    @Bean authorizationRequestManager() {
        new DefaultAuthorizationRequestManager(clientDetailsService())
    }


    //////// Endpoints (Controllers) ////////

    @Bean tokenEndpoint() {
        new TokenEndpoint (
            tokenGranter:                tokenGranter(),
            clientDetailsService:        clientDetailsService(),
            authorizationRequestManager: authorizationRequestManager()
        )
    }

    @Bean authorizationEndpoint() {
        isAuthCodeGrant || isImplicitGrant ?
            new AuthorizationEndpoint (
                tokenGranter:                tokenGranter(),
                clientDetailsService:        clientDetailsService(),
                authorizationCodeServices:   authorizationCodeServices(),
                userApprovalHandler:         clientApprovalHandler(),
                authorizationRequestManager: authorizationRequestManager(),
                userApprovalPage:            'forward:/oauth/confirm_access',
                errorPage:                   'forward:/oauth/error'
            )
        : null
    }

    @Bean approvalEndpoint() {
        new WhitelabelApprovalEndpoint()
    }

    @Bean oauth2HandlerMapping() {
        new FrameworkEndpointHandlerMapping (
            mappings: [
                '/oauth/token':     $('oaas.endpoint.token'),
                '/oauth/authorize': $('oaas.endpoint.authorization')
            ]
        )
    }
}
