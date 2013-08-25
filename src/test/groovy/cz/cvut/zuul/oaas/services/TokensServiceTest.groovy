package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.dao.AccessTokenDAO
import cz.cvut.zuul.oaas.dao.ClientDAO
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.factories.CustomGeneratorSamples.anyFutureDate
import static cz.cvut.zuul.oaas.test.factories.CustomGeneratorSamples.anyPastDate

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Mixin(ObjectFactory)
class TokensServiceTest extends Specification {

    def accessTokenDao = Mock(AccessTokenDAO)
    def clientDao = Mock(ClientDAO)

    def service = new TokensServiceImpl(
            accessTokenDAO: accessTokenDao,
            clientDAO: clientDao,
    )


    def 'get token info'() {
        given:
            def tokenVal = '123'
            def clientAuth = build(AuthorizationRequest)
            def userAuth = build(Authentication)
            def accessToken = new PersistableAccessToken(
                    build(DefaultOAuth2AccessToken).with { it.expiration = anyFutureDate(); it },
                    new OAuth2Authentication(clientAuth, userAuth)
            )
        when:
            def actual = service.getTokenInfo(tokenVal)
        then:
            1 * accessTokenDao.findOne(tokenVal) >> accessToken
            1 * clientDao.findOne(accessToken.getAuthenticatedClientId()) >> build(Client).with { it.locked = false; it }

            actual.expiresIn         == accessToken.expiresIn
            actual.scope             == accessToken.scope
            actual.audience          == clientAuth.resourceIds
            actual.clientId          == clientAuth.clientId
            actual.clientAuthorities == clientAuth.authorities as Set
            actual.userAuthorities   == userAuth.authorities as Set
            actual.userId            == userAuth.name
    }

    def 'get token info for non existing token'() {
        when:
            service.getTokenInfo('123')
        then:
            accessTokenDao.findOne(_) >> null
            thrown(InvalidTokenException)
    }

    def 'get token info for expired token'() {
        given:
            def accessToken = new PersistableAccessToken('123').with {
                it.expiration = anyPastDate(); it
            }
        when:
            service.getTokenInfo('123')
        then:
            1 * accessTokenDao.findOne(_) >> accessToken
            thrown(InvalidTokenException)
    }

    def 'get token info for locked client'() {
        setup:
            def accessToken = new PersistableAccessToken(
                    build(DefaultOAuth2AccessToken).with { it.expiration = anyFutureDate(); it },
                    build(OAuth2Authentication, [clientId: 'client-123'])
            )
            def client = build(Client).with { it.locked = true; it }

            accessTokenDao.findOne(_) >> accessToken
        when:
            service.getTokenInfo('123')
        then:
            1 * clientDao.findOne('client-123') >> client
            thrown(InvalidTokenException)
    }
}
