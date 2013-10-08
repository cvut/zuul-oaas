package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.api.models.TokenDTO
import cz.cvut.zuul.oaas.api.exceptions.NoSuchTokenException
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat
import static cz.cvut.zuul.oaas.test.CustomGeneratorSamples.anyFutureDate
import static cz.cvut.zuul.oaas.test.CustomGeneratorSamples.anyPastDate

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Mixin(CoreObjectFactory)
class TokensServiceTest extends Specification {

    def accessTokensRepo = Mock(AccessTokensRepo)
    def clientsRepo = Mock(ClientsRepo)

    def service = new TokensServiceImpl(
            accessTokensRepo: accessTokensRepo,
            clientsRepo: clientsRepo
    )

    def setup() {
        service.setupMapper()
    }


    def 'get existing token'() {
        given:
            def accessToken = build(PersistableAccessToken)
            def client = build(Client).with { it.locked = true; it }
        when:
            def actual = service.getToken('123')
        then:
            1 * accessTokensRepo.findOne('123') >> accessToken
            1 * clientsRepo.findOne(accessToken.authenticatedClientId) >> client

            actual instanceof TokenDTO
            actual.clientAuthentication.with {
                productName == client.productName
                clientLocked == client.locked
            }
            // other fields are asserted in mapping test
    }

    def 'get non existing token'() {
        when:
            service.getToken('666')
        then:
            1 * accessTokensRepo.findOne('666') >> null
            thrown(NoSuchTokenException)
    }


    def 'invalidate existing token'() {
        setup:
            accessTokensRepo.exists(_) >> true
        when:
            service.invalidateToken('666')
        then:
            1 * accessTokensRepo.delete('666')
    }

    def 'invalidate non existing token'() {
        when:
            service.invalidateToken('666')
        then:
            1 * accessTokensRepo.exists('666')
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
            1 * accessTokensRepo.findOne(tokenVal) >> accessToken
            1 * clientsRepo.findOne(accessToken.authenticatedClientId) >> build(Client).with { it.locked = false; it }

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
            accessTokensRepo.findOne(_) >> null
            thrown(NoSuchTokenException)
    }

    def 'get token info for expired token'() {
        given:
            def accessToken = new PersistableAccessToken('666').with {
                it.expiration = anyPastDate(); it
            }
        when:
            service.getTokenInfo('666')
        then:
            1 * accessTokensRepo.findOne(_) >> accessToken
            thrown(InvalidTokenException)
    }

    def 'get token info for locked client'() {
        setup:
            def accessToken = new PersistableAccessToken(
                    build(DefaultOAuth2AccessToken).with { it.expiration = anyFutureDate(); it },
                    build(OAuth2Authentication, [clientId: 'client-333'])
            )
            def client = build(Client).with { it.locked = true; it }

            accessTokensRepo.findOne(_) >> accessToken
        when:
            service.getTokenInfo('123')
        then:
            1 * clientsRepo.findOne('client-333') >> client
            thrown(InvalidTokenException)
    }


    def 'map PersistableAccessToken to TokenDTO'() {
        given:
            def client = build(PersistableAccessToken)
        when:
            def dto = service.mapper.map(client, TokenDTO)
        then:
            assertMapping client, dto
    }

    private void assertMapping(PersistableAccessToken entity, TokenDTO dto) {
        assertThat( entity ).equalsTo( dto )
                .inAllPropertiesExcept( 'clientAuthentication', 'tokenValue', 'userAuthentication' )
        assert entity.value == dto.tokenValue

        assertThat( entity.authentication.authorizationRequest )
                .equalsTo( dto.clientAuthentication )
                .inAllPropertiesExcept( 'productName', 'clientLocked' )

        assertThat( entity.authentication.userAuthentication.principal )
                .equalsTo( dto.userAuthentication )
                .inAllProperties()
    }
}
