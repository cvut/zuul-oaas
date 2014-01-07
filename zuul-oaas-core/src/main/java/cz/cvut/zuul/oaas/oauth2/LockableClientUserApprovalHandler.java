package cz.cvut.zuul.oaas.oauth2;

import cz.cvut.zuul.oaas.models.Client;
import cz.cvut.zuul.oaas.repos.ClientsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;

/**
 * This handler verifies whether the client that is to be authorized is not
 * locked. If so then the {@link ClientLockedException} is thrown, otherwise
 * the decorated parent handler is asked for approve. When no parent handler
 * was set, then {@link DefaultUserApprovalHandler} is used.
 */
@Slf4j
public class LockableClientUserApprovalHandler implements UserApprovalHandler {

    private UserApprovalHandler parentHandler = new DefaultUserApprovalHandler();
    private ClientsRepo clientsRepo;


    public AuthorizationRequest updateBeforeApproval(AuthorizationRequest authorizationRequest,
            Authentication userAuthentication) {

        return authorizationRequest;
    }

    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        Client client = clientsRepo.findOne(authorizationRequest.getClientId());

        if (client.isLocked()) {
            log.warn("Prevented authorization for locked client: [{}]", client.getClientId());
            throw new ClientLockedException("Client with id [%s] is locked", client.getClientId());
        }
        return parentHandler.isApproved(authorizationRequest, userAuthentication);
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
