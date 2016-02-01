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

import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.models.PersistableApproval
import cz.cvut.zuul.oaas.repos.ApprovalsRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import groovy.time.TimeCategory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.AuthorizationRequest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.Use

@Unroll
@Use(TimeCategory)
class AllOrNothingUserApprovalHandlerTest extends Specification {

    @Delegate CoreObjectFactory factory = new CoreObjectFactory()

    @Shared validApproval = this.&approval.curry(true, now + 1)
    @Shared deniedApproval = this.&approval.curry(false, now + 1)
    @Shared expiredApproval = this.&approval.curry(true, now - 1)

    def client = new Client(clientId: 'client-123', userApprovalRequired: true)

    def approvalsRepo = Mock(ApprovalsRepo)
    def clientsRepo = Mock(ClientsRepo) {
        findOne(client.clientId) >> client
    }

    def authzReq = new AuthorizationRequest (
        clientId: client.clientId,
        approvalParameters: [approved: 'true'],
        scope: ['scope1', 'scope2']
    )
    def userAuth = build(Authentication, [username: 'flynn'])

    def handler = new AllOrNothingUserApprovalHandler(approvalsRepo, clientsRepo).with {
        approvalParameter = 'approved';
        approvalValidity = 600; it
    }


    def 'isApproved: returns value of authorizationRequest.approved'() {
        setup:
            authzReq.approved = approved
        expect:
            handler.isApproved(authzReq, userAuth) == approved
        where:
            approved << [true, false]
    }


    def 'checkForPreApproval: approves request when client does not require user approval'() {
        setup:
            client.userApprovalRequired = false
        expect:
            handler.checkForPreApproval(authzReq, userAuth).approved
    }

    def 'checkForPreApproval: approves request when found valid approvals for all requested scopes'() {
        setup:
            approvalsRepo.findByUserIdAndClientId(userAuth.name, authzReq.clientId) >>
                [ validApproval('scope1'), validApproval('scope2') ]
        expect:
            handler.checkForPreApproval(authzReq, userAuth).approved
    }

    def 'checkForPreApproval: does not approve request when found #desc'() {
        setup:
            approvalsRepo.findByUserIdAndClientId(userAuth.name, authzReq.clientId) >> approvals
        expect:
            ! handler.checkForPreApproval(authzReq, userAuth).approved
        where:
            approvals                                                | desc
            []                                                       | 'no approvals at all'
            [ validApproval('scope3')                              ] | 'no approvals for requested scopes'
            [ validApproval('scope1')                              ] | 'valid approvals only for some of scopes'
            [ expiredApproval('scope1'), expiredApproval('scope2') ] | 'approvals for all scopes, but expired'
            [ deniedApproval('scope1'),  deniedApproval('scope2')  ] | 'approvals for all scopes, but denied'
            [ validApproval('scope1'),   expiredApproval('scope2') ] | 'approvals for all scopes, but one is expired'
    }


    def 'updateAfterApproval: approves request and saves approvals when approval parameter is true'() {
        setup:
            approvalsRepo.findByUserIdAndClientId(userAuth.name, authzReq.clientId) >>
                [ validApproval('scope2') ]
        and:
            Iterable<PersistableApproval> savedApprovals = []
        when:
            def result = handler.updateAfterApproval(authzReq, userAuth)
        then:
            result.approved
        and:
            2 * approvalsRepo.save( { savedApprovals << it } )
            savedApprovals*.scope as Set == authzReq.scope
            savedApprovals.every { it.clientId == authzReq.clientId }
            savedApprovals.every { it.userId == userAuth.name }
            savedApprovals.every { it.approved }
            savedApprovals.every {
                it.expiresAt - (now + handler.approvalValidity.seconds) < 2.seconds
            }
    }

    def 'updateAfterApproval: does not approve request when approval parameter is #desc'() {
        setup:
            authzReq.approvalParameters = approvalParams
        when:
            def result = handler.updateAfterApproval(authzReq, userAuth)
        then:
            ! result.approved
            0 * approvalsRepo._
        where:
            approvalParams      | desc
            [:]                 | 'missing'
            [approved: null]    | 'null'
            [approved: 'false'] | 'false'
    }


    def 'getUserApprovalRequest: returns requestParameters'() {
        setup:
            def requestParams = [foo: 'bar']
            def authzReq = new AuthorizationRequest(requestParameters: requestParams)
        expect:
            handler.getUserApprovalRequest(authzReq, userAuth) == requestParams
    }


    def 'setApprovalExpiry: throws AssertionError when given 0 or negative number'() {
        when:
            handler.approvalValidity = value
        then:
            thrown AssertionError
        where:
            value << [0, -1]
    }


    def approval(boolean approved, Date expiresAt, String scope) {
        new PersistableApproval('flynn', 'client-123', scope, approved, expiresAt)
    }

    def getNow() {
        new Date()
    }
}
