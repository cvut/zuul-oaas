/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.oauth2.AuthorizationCodeServicesAdapter
import cz.cvut.zuul.oaas.oauth2.ClientDetailsServiceAdapter
import cz.cvut.zuul.oaas.oauth2.LockableClientUserApprovalHandler
import cz.cvut.zuul.oaas.oauth2.TokenStoreAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.provider.CompositeTokenGranter
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter
import org.springframework.security.oauth2.provider.endpoint.*
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

/**
 * Configuration of non-security features of the Authorization Server endpoints,
 * like token store, token customizations, user approvals and grant types.
 */
@Configuration
class AuthorizationServerConfig implements ConfigurationSupport {

    @Autowired PersistenceBeans repos

    @Autowired @Qualifier('client')
    AuthenticationManager clientAuthManager

    @Value('${oaas.refresh_token.enabled}') boolean isRefreshToken
    @Value('${oaas.grant.implicit.enabled}') boolean isImplicitGrant
    @Value('${oaas.grant.authorization_code.enabled}') boolean isAuthCodeGrant


    //////// Endpoints (Controllers) ////////

    @Bean tokenEndpoint() {
        new TokenEndpoint (
            tokenGranter:         tokenGranter(),
            clientDetailsService: clientDetailsService(),
            OAuth2RequestFactory: oAuth2RequestFactory()
        )
    }


    @Bean authorizationEndpoint() {
        if (isAuthCodeGrant || isImplicitGrant) {
            new AuthorizationEndpoint(
                tokenGranter:              tokenGranter(),
                clientDetailsService:      clientDetailsService(),
                OAuth2RequestFactory:      oAuth2RequestFactory(),
                authorizationCodeServices: authorizationCodeServices(),
                userApprovalHandler:       userApprovalHandler(),
                userApprovalPage:          'forward:/oauth/confirm_access',
                errorPage:                 'forward:/oauth/error'
            )
        } else null
    }

    @Bean approvalEndpoint() {
        new WhitelabelApprovalEndpoint()
    }

    @Bean checkTokenEndpoint() {
        new CheckTokenEndpoint( resourceServerTokenServices() )
    }

    @Bean oauth2HandlerMapping() {
        new FrameworkEndpointHandlerMapping (
            mappings: [
                '/oauth/token':       p('oaas.endpoint.token.uri'),
                '/oauth/authorize':   p('oaas.endpoint.authorization.uri'),
                '/oauth/check_token': p('oaas.endpoint.check_token.uri')
            ]
        )
    }

    //////// Services etc. ////////

    @Bean tokenGranter() {
        def granters = []

        if ( isAuthCodeGrant ) {
            granters << new AuthorizationCodeTokenGranter (
                tokenServices(),
                authorizationCodeServices(),
                clientDetailsService(),
                oAuth2RequestFactory()
            )
        }
        if ( isRefreshToken ) {
            granters << new RefreshTokenGranter (
                tokenServices(),
                clientDetailsService(),
                oAuth2RequestFactory()
            )
        }
        if ( isImplicitGrant ) {
            granters << new ImplicitTokenGranter (
                tokenServices(),
                clientDetailsService(),
                oAuth2RequestFactory()
            )
        }
        if ( p('oaas.grant.client_credentials.enabled') as boolean ) {
            granters << new ClientCredentialsTokenGranter (
                tokenServices(),
                clientDetailsService(),
                oAuth2RequestFactory()
            )
        }
        if ( p('oaas.grant.password.enabled') as boolean ) {
            granters << new ResourceOwnerPasswordTokenGranter (
                clientAuthManager,
                tokenServices(),
                clientDetailsService(),
                oAuth2RequestFactory()
            )
        }
        new CompositeTokenGranter(granters)
    }

    @Bean tokenServices() {
        new DefaultTokenServices (
            supportRefreshToken:         isRefreshToken,
            accessTokenValiditySeconds:  p('oaas.access_token.validity') as int,
            refreshTokenValiditySeconds: p('oaas.refresh_token.validity') as int,
            reuseRefreshToken:           p('oaas.refresh_token.reuse') as boolean,
            clientDetailsService:        clientDetailsService(),
            tokenStore:                  tokenStore()
        )
    }

    // Register explicitly under this interface so it can be autowired by this type.
    @Bean ResourceServerTokenServices resourceServerTokenServices() {
        tokenServices()
    }

    // Register explicitly under this interface so it can be autowired by this type.
    @Bean ConsumerTokenServices consumerTokenServices() {
        tokenServices()
    }

    @Bean userApprovalHandler() {
        def handler = new TokenStoreUserApprovalHandler (
            tokenStore:           tokenStore(),
            clientDetailsService: clientDetailsService(),
            requestFactory:       oAuth2RequestFactory()
        )
        new LockableClientUserApprovalHandler(handler, repos.clientsRepo())
    }

    @Bean oAuth2RequestFactory() {
        new DefaultOAuth2RequestFactory( clientDetailsService() )
    }

    @Bean clientDetailsService() {
        new ClientDetailsServiceAdapter( repos.clientsRepo() )
    }

    @Bean @Lazy authorizationCodeServices() {
        new AuthorizationCodeServicesAdapter( repos.authorizationCodesRepo() )
    }

    @Bean tokenStore() {
        new TokenStoreAdapter( repos.accessTokensRepo(), repos.refreshTokensRepo() )
    }
}
