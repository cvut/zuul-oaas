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
package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.api.exceptions.NoSuchTokenException
import cz.cvut.zuul.oaas.api.models.TokenDTO
import cz.cvut.zuul.oaas.api.models.TokenInfo
import cz.cvut.zuul.oaas.api.services.TokensService
import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.models.User
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import groovy.transform.PackageScope
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

@Service
public class TokensServiceImpl implements TokensService {

    AccessTokensRepo accessTokensRepo

    ClientsRepo clientsRepo

    /**
     * Orika Mapper Factory to be configured and used for mapping between entity
     * and DTO objects. If no factory is provided, then new one will be created.
     *
     * @see {@link #setupMapper()}
     */
    MapperFactory mapperFactory

    @PackageScope MapperFacade mapper



    TokenDTO getToken(String tokenValue) {

        def accessToken = accessTokensRepo.findOne(tokenValue)
        if (!accessToken) {
            throw new NoSuchTokenException("No such token: ${tokenValue}")
        }

        def client = clientsRepo.findOne(accessToken.authenticatedClientId)

        def dto = mapper.map(accessToken, TokenDTO)
        // TODO what is the purpose?
        dto.clientAuthentication.clientLocked = client.locked
        dto.clientAuthentication.productName = client.productName

        return dto
    }

    TokenInfo getTokenInfo(String tokenValue) {

        def accessToken = accessTokensRepo.findOne(tokenValue)

        // first check if token is recognized and if it is not expired
        if (!accessToken) {
            throw new NoSuchTokenException('Token was not recognised')
        }
        if (accessToken.expired) {
            throw new InvalidTokenException('Token has expired')
        }

        def client = clientsRepo.findOne(accessToken.authenticatedClientId)
        if (!client) {
            throw new InvalidTokenException("Client doesn't exist anymore")
        }
        if (client.locked) {
            throw new InvalidTokenException('Client is locked')
        }

        def clientAuth = accessToken.authentication.getOAuth2Request()
        def userAuth = accessToken.authentication.userAuthentication
        def user = (User) userAuth?.principal instanceof User ? userAuth.principal : null

        new TokenInfo (
            expiresIn: accessToken.expiresIn,
            scope: accessToken.scope,
            audience: clientAuth.resourceIds,
            clientId: clientAuth.clientId,
            clientAuthorities: clientAuth.authorities,
            userAuthorities: userAuth?.authorities,
            userId: userAuth?.name,
            userEmail: user?.email
        )
    }

    void invalidateToken(String tokenValue) {

        if (!accessTokensRepo.exists(tokenValue)) {
            throw new NoSuchTokenException("No such token: ${tokenValue}")
        }
        accessTokensRepo.delete(tokenValue)
    }


    @PostConstruct setupMapper() {
        def factory = mapperFactory ?: new Builder().build()

        factory.classMap(PersistableAccessToken, TokenDTO)
                .field('value', 'tokenValue')
                .fieldAToB('authentication.OAuth2Request', 'clientAuthentication')
                .fieldAToB('authentication.userAuthentication.principal', 'userAuthentication')
                .exclude('metaClass')
                .byDefault().register()

        factory.classMap(OAuth2Request, TokenDTO.ClientAuthentication)
                .byDefault().register()

        factory.classMap(User, TokenDTO.UserAuthentication)
                .exclude('metaClass')
                .byDefault().register()

        mapper = factory.mapperFacade
    }
}
