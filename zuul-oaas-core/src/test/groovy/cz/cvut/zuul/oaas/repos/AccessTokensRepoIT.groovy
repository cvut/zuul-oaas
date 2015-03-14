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
package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.models.PersistableAccessToken
import cz.cvut.zuul.oaas.test.SharedAsserts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

class AccessTokensRepoIT extends AbstractRepoIT<PersistableAccessToken> {

    @Autowired AccessTokensRepo repo

    String idPropertyName = 'value'

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
            repo.save(expected)
        when:
            def actual = repo.findOne(expected.value)
        then:
            assertIt actual, expected
    }

    def 'find token by non existing authentication'() {
        setup:
            def invalid = build(OAuth2Authentication)
            repo.save(seed())
        expect:
            repo.findOneByAuthentication(invalid) == null
    }

    def 'find token by authentication'() {
        setup:
            def expectedToken = build(OAuth2AccessToken)
            def authentication = build(OAuth2Authentication, [clientOnly: true])

            repo.save(new PersistableAccessToken(expectedToken, authentication))
        when:
            def actualToken = repo.findOneByAuthentication(authentication)
        then:
            expectedToken == actualToken
    }

    def 'find tokens by clientId'() {
        setup:
            repo.save(seed())
            2.times {
                def entity = new PersistableAccessToken(
                        build(OAuth2AccessToken),
                        build(OAuth2Authentication, [clientId: 'someClientId'])
                )
                repo.save(entity)
            }
        when:
            def result = repo.findByClientId('someClientId')
        then:
            result.size() == 2
    }

    def 'find tokens by username'() {
        setup:
            repo.save(seed())
            2.times {
                def entity = new PersistableAccessToken(
                        build(OAuth2AccessToken),
                        build(OAuth2Authentication, [clientId: 'someClientId', username: 'myName'])
                )
                repo.save(entity)
            }
        when:
            def result = repo.findByClientIdAndUserName('someClientId', 'myName')
        then:
            result.size() == 2
    }

    def 'delete token by refresh token'() {
        setup:
            def refreshToken = build(OAuth2RefreshToken)
            def accessToken = build(OAuth2AccessToken, [refreshToken: refreshToken])
            def entity = new PersistableAccessToken(accessToken, build(OAuth2Authentication, [clientOnly: true]))

            repo.save(entity)
            assert repo.exists(accessToken.value)
        when:
            repo.deleteByRefreshToken(refreshToken)
        then:
            ! repo.exists(refreshToken.value)
    }

    def 'delete token by clientId'() {
        setup:
            def clientId = 'someClientId'
            def accessToken = new PersistableAccessToken(
                    build(OAuth2AccessToken),
                    build(OAuth2Authentication, [clientId: clientId])
            )

            repo.save(accessToken)
            assert repo.exists(accessToken.value)
        when:
            repo.deleteByClientId(clientId)
        then:
            ! repo.exists(accessToken.value)
    }
}
