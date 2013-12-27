package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.oauth2.ClientDetailsServiceImpl
import cz.cvut.zuul.oaas.oauth2.LockableClientUserApprovalHandler
import cz.cvut.zuul.oaas.oauth2.TokenStoreImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

import javax.inject.Inject

@Configuration
@ImportResource('classpath:/cz/cvut/zuul/oaas/config/authorization-server.xml')
class AuthorizationServerConfig extends ConfigurationSupport {

    @Inject PersistenceBeans repos

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
            supportRefreshToken:         true,
            accessTokenValiditySeconds:  $('oaas.access_token.validity') as int,
            refreshTokenValiditySeconds: $('oaas.refresh_token.validity') as int,
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
}
