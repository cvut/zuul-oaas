package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.models.PersistableRefreshToken
import cz.cvut.zuul.oaas.test.SharedAsserts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class RefreshTokensRepoIT extends AbstractRepoIT<PersistableRefreshToken> {

    @Autowired RefreshTokensRepo repo

    PersistableRefreshToken buildEntity() {
        new PersistableRefreshToken(
                build(OAuth2RefreshToken),
                build(OAuth2Authentication)
        )
    }

    void assertIt(PersistableRefreshToken actual, PersistableRefreshToken expected) {
        assertThat (actual) equalsTo (expected) inAllPropertiesExcept ('authentication')
        SharedAsserts.isEqual actual.authentication, expected.authentication
    }

    def 'return expiring token'() {
        given:
            def expected = new PersistableRefreshToken(
                    build(ExpiringOAuth2RefreshToken),
                    build(OAuth2Authentication, [clientOnly: true])
            )
            repo.save(expected)
        when:
            def actual = repo.findOne(expected.value)
        then:
            assertIt actual, expected
            actual.isExpiring()
    }

    def 'return token with client-only authentication'() {
        given:
            def expected = new PersistableRefreshToken(
                    build(OAuth2RefreshToken),
                    build(OAuth2Authentication, [clientOnly: true])
            )
            repo.save(expected)
        when:
            def actual = repo.findOne(expected.value)
        then:
            assertIt actual, expected
    }

    def 'delete token by clientId'() {
        setup:
            def clientId = 'someClientId'
            def refreshToken = new PersistableRefreshToken(
                    build(OAuth2RefreshToken),
                    build(OAuth2Authentication, [clientId: clientId])
            )

            repo.save(refreshToken)
            assert repo.exists(refreshToken.value)
        when:
            repo.deleteByClientId(clientId)
        then:
            ! repo.exists(refreshToken.value)
    }
}
