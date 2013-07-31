package cz.cvut.zuul.oaas.dao

import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import cz.cvut.zuul.oaas.test.spock.MongoCleanup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@MongoCleanup
@Mixin(ObjectFactory)
@ContextConfiguration('classpath:dao-test.xml')
class AccessTokenDAO_IT extends Specification {

    @Autowired AccessTokenDAO dao


    def 'save token'() {
        given:
            def token = build(OAuth2AccessToken)
            def auth = build(OAuth2Authentication, [clientOnly: true])
        when:
            dao.save(new PersistableAccessToken(token, auth))
        then:
            ! dao.findAll().toList().isEmpty()
    }

    def 'find token by non existing id'() {
        expect:
            dao.findOne('tokenThatDoesNotExist') == null
    }

    def 'find by id and check token only'() {
        setup:
            def expected = build(OAuth2AccessToken)
            def auth = build(OAuth2Authentication, [clientOnly: true])

            assert dao.save(new PersistableAccessToken(expected, auth))
        when:
            def actual = dao.findOne(expected.value)
        then:
            assertThat (actual) equalsTo (expected) inAllPropertiesExcept ('authentication')
    }

    def 'find by id and check token with authorization request and client authentication'() {
        setup:
            def expectToken = build(OAuth2AccessToken)
            def expectAuthzReq = build(AuthorizationRequest)
            def expectUserAuth = build(Authentication)
            def expectOAuthAuth = new OAuth2Authentication(expectAuthzReq, expectUserAuth)

            assert dao.save(new PersistableAccessToken(expectToken, expectOAuthAuth))
        when:
            def actualToken = dao.findOne(expectToken.value)
        then:
            def actualOAuthAuth = actualToken.authentication
            def actualAuthzReq = actualOAuthAuth.authorizationRequest
            def actualUserAuth = actualOAuthAuth.userAuthentication

            assertThat (actualToken) equalsTo (expectToken) inAllPropertiesExcept ('authentication')

            ! actualOAuthAuth.clientOnly
            assertThat (actualOAuthAuth) equalsTo expectOAuthAuth inAllPropertiesExcept ('userAuthentication', 'authorizationRequest', 'authorities')

            actualOAuthAuth.authorities as Set == expectOAuthAuth.authorities as Set

            assertThat (actualAuthzReq) equalsTo (expectAuthzReq) inAllPropertiesExcept ('authorizationParameters')

            // scope parameter contains space separated scopes extracted from property of type Set,
            // thus it can be shuffled so we must exclude it from assertion
            def actualAuthzParams = actualAuthzReq.authorizationParameters.findAll { it.key != 'scope' }
            def expectedAuthzParams =  expectAuthzReq.authorizationParameters.findAll { it.key != 'scope' }
            actualAuthzParams == expectedAuthzParams

            assertThat (actualUserAuth) equalsTo (expectUserAuth) inAllPropertiesExcept ('authorities')
            actualUserAuth.authorities as Set == expectUserAuth.authorities as Set
    }

    def 'find by id and check token with client only authentication'() {
        given:
            def expectedToken = build(OAuth2AccessToken)
            def expectedAuth = new OAuth2Authentication(
                    build(AuthorizationRequest), null)

            assert dao.save(new PersistableAccessToken(expectedToken, expectedAuth))
        when:
            def actualToken = dao.findOne(expectedToken.value)
        then:
            def actualAuth = actualToken.authentication

            assertThat (actualToken) equalsTo (expectedToken) inAllPropertiesExcept ('authentication')

            actualAuth.clientOnly
            actualAuth.userAuthentication == null
            // other properties are already asserted in previous test
    }

    def 'find token by non existing authentication'() {
        given:
            def auth = build(OAuth2Authentication)
        expect:
            dao.findOneByAuthentication(auth) == null
    }

    def 'find token by existing authentication'() {
        setup:
            def expectedToken = build(OAuth2AccessToken)
            def authentication = build(OAuth2Authentication, [clientOnly: true])

            assert dao.save(new PersistableAccessToken(expectedToken, authentication))
        when:
            def actualToken = dao.findOneByAuthentication(authentication)
        then:
            expectedToken == actualToken
    }


    def 'remove token by id'() {
        setup:
            def accessToken = new PersistableAccessToken(
                    build(OAuth2AccessToken),
                    build(OAuth2Authentication)
            )
            assert dao.save(accessToken)
            assert dao.findOne(accessToken.value) != null
        when:
            dao.delete(accessToken.value)
        then:
            dao.findOne(accessToken.value) == null
    }

    def 'remove token by refresh token'() {
        setup:
            def refreshToken = build(OAuth2RefreshToken)
            def accessToken = build(OAuth2AccessToken, [refreshToken: refreshToken])

            assert dao.save(new PersistableAccessToken(accessToken, build(OAuth2Authentication, [clientOnly: true])))
            assert dao.findOne(accessToken.value)
        when:
            dao.deleteByRefreshToken(refreshToken)
        then:
            ! dao.findOne(refreshToken.value)
    }


    def 'find tokens by clientId'() {
        setup:
            2.times {
                def token = build(OAuth2AccessToken)
                def auth = build(OAuth2Authentication, [clientId: 'someClientId'])

                assert dao.save(new PersistableAccessToken(token, auth))
            }
        when:
            def result = dao.findByClientId('someClientId')
        then:
            result.size() == 2
    }

    def 'find tokens by username'() {
        setup:
            2.times {
                def token = build(OAuth2AccessToken)
                def auth = build(OAuth2Authentication, [username: 'myName'])

                assert dao.save(new PersistableAccessToken(token, auth))
            }
        when:
            def result = dao.findByUserName('myName')
        then:
            result.size() == 2
    }
}
