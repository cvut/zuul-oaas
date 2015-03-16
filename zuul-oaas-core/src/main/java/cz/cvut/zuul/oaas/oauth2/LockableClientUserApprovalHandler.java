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
package cz.cvut.zuul.oaas.oauth2;

import cz.cvut.zuul.oaas.models.Client;
import cz.cvut.zuul.oaas.repos.ClientsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;

import java.util.Map;

/**
 * This handler verifies whether the client that is to be authorized is not
 * locked. If so then the {@link ClientLockedException} is thrown, otherwise
 * the decorated parent handler is asked for approve. When no parent handler
 * was set, then {@link DefaultUserApprovalHandler} is used.
 *
 * This is important especially for clients with implicit grant which are
 * issued an access token directly without requesting token endpoint.
 */
@Slf4j
public class LockableClientUserApprovalHandler implements UserApprovalHandler {

    private UserApprovalHandler parentHandler = new DefaultUserApprovalHandler();
    private ClientsRepo clientsRepo;


    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        Client client = clientsRepo.findOne(authorizationRequest.getClientId());

        if (client.isLocked()) {
            log.warn("Prevented authorization for locked client: [{}]", client.getClientId());
            throw new ClientLockedException("Client with id [%s] is locked", client.getClientId());
        }
        return parentHandler.isApproved(authorizationRequest, userAuthentication);
    }

    public AuthorizationRequest checkForPreApproval(AuthorizationRequest authzRequest, Authentication userAuth) {
        return parentHandler.checkForPreApproval(authzRequest, userAuth);
    }

    public AuthorizationRequest updateAfterApproval(AuthorizationRequest authzRequest, Authentication userAuth) {
        return parentHandler.updateAfterApproval(authzRequest, userAuth);
    }

    public Map<String, Object> getUserApprovalRequest(AuthorizationRequest authzRequest, Authentication userAuth) {
        return parentHandler.getUserApprovalRequest(authzRequest, userAuth);
    }

    /**
     * The parent handler that is asked for approve when client is not locked.
     * When no parent handler is provided then {@link DefaultUserApprovalHandler}
     * is used.
     *
     * @param parentHandler
     */
    public void setParentHandler(UserApprovalHandler parentHandler) {
        this.parentHandler = parentHandler;
    }

    @Required
    public void setClientsRepo(ClientsRepo clientsRepo) {
        this.clientsRepo = clientsRepo;
    }
}
