package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.api.resources.exceptions.NoSuchTokenException
import cz.cvut.zuul.oaas.dao.AccessTokenDAO
import cz.cvut.zuul.oaas.dao.ClientDAO
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat
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
            clientDAO: clientDao
    )

    def setup() {
        service.setupMapper()
    }


    def 'get existing token'() {
        given:
            def tokenVal = '123'
            def clientAuth = build(AuthorizationRequest)
            def userAuth = build(Authentication)
            def accessToken = new PersistableAccessToken(
                    build(OAuth2AccessToken),
                    new OAuth2Authentication(clientAuth, userAuth)
            )
            def client = build(Client).with { it.locked = true; it }
        when:
            def actual = service.getToken(tokenVal)
        then:
            1 * accessTokenDao.findOne(tokenVal) >> accessToken
            1 * clientDao.findOne(accessToken.authenticatedClientId) >> client

            assertThat( actual ).equalsTo( accessToken ).inProperties(
                    'expiration', 'scope', 'tokenType'
            )
            actual.tokenValue == accessToken.value

            assertThat( actual.clientAuthentication ).equalsTo( clientAuth ).inProperties(
                    'clientId', 'scope', 'redirectUri', 'resourceIds'
            )
            with (actual.clientAuthentication) {
                clientLocked == client.locked
                productName  == client.productName
            }

            assertThat( actual.userAuthentication ).equalsTo( userAuth.principal ).inProperties(
                    'username', 'email', 'firstName', 'lastName'
            )
    }

    def 'get non existing token'() {
        when:
            service.getToken('666')
        then:
            1 * accessTokenDao.findOne('666') >> null
            thrown(NoSuchTokenException)
    }


    def 'invalidate existing token'() {
        setup:
            accessTokenDao.exists(_) >> true
        when:
            service.invalidateToken('666')
        then:
            1 * accessTokenDao.delete('666')
    }

    def 'invalidate non existing token'() {
        when:
            service.invalidateToken('666')
        then:
            1 * accessTokenDao.exists('666')
            thrown(NoSuchTokenException)
    }


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
            1 * clientDao.findOne(accessToken.authenticatedClientId) >> build(Client).with { it.locked = false; it }

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
            service.getTokenInfo('666')
        then:
            accessTokenDao.findOne(_) >> null
            thrown(InvalidTokenException)
    }

    def 'get token info for expired token'() {
        given:
            def accessToken = new PersistableAccessToken('666').with {
                it.expiration = anyPastDate(); it
            }
        when:
            service.getTokenInfo('666')
        then:
            1 * accessTokenDao.findOne(_) >> accessToken
            thrown(InvalidTokenException)
    }

    def 'get token info for locked client'() {
        setup:
            def accessToken = new PersistableAccessToken(
                    build(DefaultOAuth2AccessToken).with { it.expiration = anyFutureDate(); it },
                    build(OAuth2Authentication, [clientId: 'client-333'])
            )
            def client = build(Client).with { it.locked = true; it }

            accessTokenDao.findOne(_) >> accessToken
        when:
            service.getTokenInfo('123')
        then:
            1 * clientDao.findOne('client-333') >> client
            thrown(InvalidTokenException)
    }
}
