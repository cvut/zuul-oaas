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
package cz.cvut.zuul.oaas.oauth2

import cz.cvut.zuul.oaas.models.PersistableApproval
import cz.cvut.zuul.oaas.repos.ApprovalsRepo
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import org.springframework.security.oauth2.provider.approval.Approval
import spock.lang.Specification

class ApprovalStoreImplTest extends Specification {

    @Delegate
    CoreObjectFactory factory = new CoreObjectFactory()

    def repo = Mock(ApprovalsRepo)
    def store = new ApprovalStoreImpl(repo)

    def approvals = buildListOf(Approval)


    def "addApprovals: saves the approval to the repo"() {
        setup:
            def expected = approvals.collect { new PersistableApproval(it) }
        when:
            def result = store.addApprovals(approvals)
        then:
            1 * repo.saveAll({ it == expected })
            result
    }

    def "revokeApprovals: deletes the approvals from the repo"() {
        when:
            def result = store.revokeApprovals(approvals)
        then:
            approvals.each {
                1 * repo.deleteById(it.userId, it.clientId, it.scope)
            }
            result
    }

    def "getApprovals: finds approvals in the repo by the userId and clientId and returns it"() {
        setup:
            def persApprovals = buildListOf(PersistableApproval)
        when:
            def result = store.getApprovals('flynn', 'abc')
        then:
            1 * repo.findByUserIdAndClientId('flynn', 'abc') >> persApprovals
            result == persApprovals*.toOAuthApproval()
    }
}
