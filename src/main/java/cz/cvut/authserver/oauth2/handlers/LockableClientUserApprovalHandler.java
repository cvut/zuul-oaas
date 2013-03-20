package cz.cvut.authserver.oauth2.handlers;

import cz.cvut.authserver.oauth2.dao.ClientDAO;
import cz.cvut.authserver.oauth2.models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class LockableClientUserApprovalHandler implements UserApprovalHandler {

    public static final Logger LOG = LoggerFactory.getLogger(LockableClientUserApprovalHandler.class);

    private UserApprovalHandler parentHandler = new DefaultUserApprovalHandler();
    private ClientDAO clientDAO;


    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        Client client = clientDAO.findOne(authorizationRequest.getClientId());

        if (client.isLocked()) {
            LOG.warn("Prevented authorization for locked client: [{}]", client.getClientId());
            throw new ClientLockedException(String.format(
                    "Client with id [%s] is locked", client.getClientId()));
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
    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }
}
