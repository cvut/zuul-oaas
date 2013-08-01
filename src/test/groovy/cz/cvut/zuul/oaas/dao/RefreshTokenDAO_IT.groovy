package cz.cvut.zuul.oaas.dao

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
class RefreshTokenDAO_IT extends AbstractDAO_IT<PersistableRefreshToken> {

    @Autowired RefreshTokenDAO dao

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
            dao.save(expected)
        when:
            def actual = dao.findOne(expected.value)
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
            dao.save(expected)
        when:
            def actual = dao.findOne(expected.value)
        then:
            assertIt actual, expected
    }
}
