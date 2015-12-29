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
import cz.cvut.zuul.oaas.repos.ClientsRepo
import groovy.time.TimeCategory
import groovy.util.logging.Slf4j
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.util.OAuth2Utils
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler

/**
 * Simple user approval handler that remembers approval decisions and gives user
 * an option to approve all scopes, or nothing. Request is automatically approved
 * if the client's {@code userApprovalRequired} is false.
 */
@Slf4j
class AllOrNothingUserApprovalHandler implements UserApprovalHandler {

    final ApprovalsRepo approvalsRepo
    final ClientsRepo clientsRepo

    /**
     * Name of form parameter that contains approval decision.
     */
    String approvalParameter = OAuth2Utils.USER_OAUTH_APPROVAL

    /**
     * Expiration time of a user approval in seconds. It must be greater than 0.
     * Default is 2,592,000 seconds (30 days).
     */
    int approvalValidity = 2592000



    AllOrNothingUserApprovalHandler(ApprovalsRepo approvalsRepo, ClientsRepo clientsRepo) {
        assert approvalsRepo != null
        assert clientsRepo != null

        this.approvalsRepo = approvalsRepo
        this.clientsRepo = clientsRepo
    }


    boolean isApproved(AuthorizationRequest authzReq, Authentication userAuth) {
        authzReq.isApproved()
    }

    AuthorizationRequest checkForPreApproval(AuthorizationRequest authzReq, Authentication userAuth) {

        if (isUserApprovalRequired(authzReq.clientId)) {
            log.debug 'Looking up user approved authorizations for client_id = {} and user = {}',
                authzReq.clientId, userAuth.name

            def approvedScopes = approvedScopes(userAuth.name, authzReq.clientId)

            if (approvedScopes.containsAll(authzReq.scope)) {
                log.debug 'User has already approved all requested scopes: {}', authzReq.scope
                authzReq.approved = true
            }
        } else {
            log.debug 'User approval is not required for client_id = {}', authzReq.clientId
            authzReq.approved = true
        }

        authzReq
    }

    AuthorizationRequest updateAfterApproval(AuthorizationRequest authzReq, Authentication userAuth) {

        if (authzReq.approvalParameters[approvalParameter]?.toBoolean()) {
            log.info 'User {} approved authorization for client {}', userAuth.name, authzReq.clientId

            approvalsRepo.saveAll(authzReq.scope.collect { scope ->
                new PersistableApproval(userAuth.name, authzReq.clientId, scope, true, expiresAt)
            })
            authzReq.approved = true

        } else {
            log.info 'User {} denied authorization for client {}', userAuth.name, authzReq.clientId
        }

        authzReq
    }

    Map<String, Object> getUserApprovalRequest(AuthorizationRequest authzReq, Authentication userAuth) {
        authzReq.requestParameters
    }


    void setApprovalValidity(int approvalExpiry) {
        assert approvalExpiry > 0
        this.approvalValidity = approvalExpiry
    }


    // TODO: move to repository
    private approvedScopes(String userId, String clientId) {
        def now = new Date()

        approvalsRepo
            .findByUserIdAndClientId(userId, clientId)
            .findAll { it.approved }
            .findAll { it.expiresAt.after(now) }
            .collect { it.scope }
    }

    private isUserApprovalRequired(String clientId) {

        def client = clientsRepo.findOne(clientId)
        assert client != null, "Could not find Client with id = ${clientId}"

        client.userApprovalRequired
    }

    private getExpiresAt() {
        use (TimeCategory) {
            new Date() + approvalValidity.seconds
        }
    }
}
