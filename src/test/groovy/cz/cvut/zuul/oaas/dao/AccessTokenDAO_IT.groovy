package cz.cvut.zuul.oaas.dao

import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.test.SharedAsserts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class AccessTokenDAO_IT extends AbstractDAO_IT<PersistableAccessToken> {

    @Autowired AccessTokenDAO dao

    def idPropertyName = 'value'

    PersistableAccessToken buildEntity() {
        new PersistableAccessToken(
                build(OAuth2AccessToken),
                build(OAuth2Authentication)
        )
    }

    void assertIt(PersistableAccessToken actual, PersistableAccessToken expected) {
        assertThat (actual) equalsTo (expected) inAllPropertiesExcept ('authentication')
        SharedAsserts.isEqual actual.authentication, expected.authentication
    }


    def 'return token with client-only authentication'() {
        given:
            def expected = new PersistableAccessToken(
                    build(OAuth2AccessToken),
                    build(OAuth2Authentication, [clientOnly: true])
            )
            dao.save(expected)
        when:
            def actual = dao.findOne(expected.value)
        then:
            assertIt actual, expected
    }

    def 'find token by non existing authentication'() {
        setup:
            def invalid = build(OAuth2Authentication)
            dao.save(seed())
        expect:
            dao.findOneByAuthentication(invalid) == null
    }

    def 'find token by authentication'() {
        setup:
            def expectedToken = build(OAuth2AccessToken)
            def authentication = build(OAuth2Authentication, [clientOnly: true])

            dao.save(new PersistableAccessToken(expectedToken, authentication))
        when:
            def actualToken = dao.findOneByAuthentication(authentication)
        then:
            expectedToken == actualToken
    }

    def 'find tokens by clientId'() {
        setup:
            dao.save(seed())
            2.times {
                def entity = new PersistableAccessToken(
                        build(OAuth2AccessToken),
                        build(OAuth2Authentication, [clientId: 'someClientId'])
                )
                dao.save(entity)
            }
        when:
            def result = dao.findByClientId('someClientId')
        then:
            result.size() == 2
    }

    def 'find tokens by username'() {
        setup:
            dao.save(seed())
            2.times {
                def entity = new PersistableAccessToken(
                        build(OAuth2AccessToken),
                        build(OAuth2Authentication, [username: 'myName'])
                )
                dao.save(entity)
            }
        when:
            def result = dao.findByUserName('myName')
        then:
            result.size() == 2
    }

    def 'delete token by refresh token'() {
        setup:
            def refreshToken = build(OAuth2RefreshToken)
            def accessToken = build(OAuth2AccessToken, [refreshToken: refreshToken])
            def entity = new PersistableAccessToken(accessToken, build(OAuth2Authentication, [clientOnly: true]))

            dao.save(entity)
            assert dao.exists(accessToken.value)
        when:
            dao.deleteByRefreshToken(refreshToken)
        then:
            ! dao.exists(refreshToken.value)
    }

    def 'delete token by clientId'() {
        setup:
            def clientId = 'someClientId'
            def accessToken = new PersistableAccessToken(
                    build(OAuth2AccessToken),
                    build(OAuth2Authentication, [clientId: clientId])
            )

            dao.save(accessToken)
            assert dao.exists(accessToken.value)
        when:
            dao.deleteByClientId(clientId)
        then:
            ! dao.exists(accessToken.value)
    }
}
