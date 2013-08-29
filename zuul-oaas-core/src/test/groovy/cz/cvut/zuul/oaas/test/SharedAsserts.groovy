package cz.cvut.zuul.oaas.test

import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class SharedAsserts {

    static isEqual(OAuth2Authentication actual, OAuth2Authentication expected) {
        def actualAuthzReq = actual.authorizationRequest
        def expectAuthzReq = expected.authorizationRequest
        def actualUserAuth = actual.userAuthentication
        def expectUserAuth = expected.userAuthentication

        assertThat (actual) equalsTo expected inAllPropertiesExcept ('userAuthentication', 'authorizationRequest', 'authorities')

        actual.authorities as Set == expected.authorities as Set

        assertThat (actualAuthzReq) equalsTo (expectAuthzReq) inAllPropertiesExcept ('authorizationParameters')

        // scope parameter contains space separated scopes extracted from property of type Set,
        // thus it can be shuffled so we must exclude it from assertion
        def actualAuthzParams = actualAuthzReq.authorizationParameters.findAll { it.key != 'scope' }
        def expectedAuthzParams =  expectAuthzReq.authorizationParameters.findAll { it.key != 'scope' }
        actualAuthzParams == expectedAuthzParams

        if (expectUserAuth) {
            assertThat (actualUserAuth) equalsTo (expectUserAuth) inAllPropertiesExcept ('authorities')
            assert actualUserAuth.authorities as Set == expectUserAuth.authorities as Set
        }
    }
}
