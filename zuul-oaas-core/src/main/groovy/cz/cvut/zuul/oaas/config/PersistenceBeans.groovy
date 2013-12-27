package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo
import cz.cvut.zuul.oaas.repos.ResourcesRepo
import org.springframework.context.annotation.Bean

interface PersistenceBeans {

    @Bean ClientsRepo clientsRepo()

    @Bean AccessTokensRepo accessTokensRepo()

    @Bean RefreshTokensRepo refreshTokensRepo()

    @Bean ResourcesRepo resourcesRepo()
}
