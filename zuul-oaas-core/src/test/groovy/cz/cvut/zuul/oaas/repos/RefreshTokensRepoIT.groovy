/*
 * The MIT License
 *
 * Copyright 2013-2016 Czech Technical University in Prague.
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

import cz.cvut.zuul.oaas.models.PersistableRefreshToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication

abstract class RefreshTokensRepoIT extends BaseRepositoryIT<PersistableRefreshToken> {

    @Autowired RefreshTokensRepo repo


    PersistableRefreshToken buildEntity() {
        new PersistableRefreshToken(
                build(OAuth2RefreshToken),
                build(OAuth2Authentication)
        )
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
