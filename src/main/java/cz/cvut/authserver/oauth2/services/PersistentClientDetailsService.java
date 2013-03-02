package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.dao.ClientDAO;
import cz.cvut.authserver.oauth2.models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.*;

import java.util.List;

/**
 * MongoDB implementation of the Client details and registration service.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class PersistentClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(PersistentClientDetailsService.class);

    private ClientDAO clientDao;
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();



    public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {
        ClientDetails result = clientDao.findOne(clientId);

        if (result == null) {
            throw OAuth2Exception.create(OAuth2Exception.INVALID_CLIENT, "No such client found with id = " + clientId);
        }
        return result;
    }

    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        try {
            LOG.debug("Adding client: {}", clientDetails.getClientId());
            clientDao.save(new Client(encodeClientSecret(clientDetails)));

        } catch (DuplicateKeyException ex) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId(), ex);
        }
    }

    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        LOG.debug("Updating client: {}", clientDetails.getClientId());
        try {
            clientDao.update(new Client(clientDetails));

        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException(ex.getMessage(), ex);
        }
    }

    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        LOG.debug("Updating secret for client: {}", clientId);
        try {
            clientDao.updateClientSecret(clientId, passwordEncoder.encode(secret));

        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException(ex.getMessage(), ex);
        }
    }

    public void removeClientDetails(String clientId) throws NoSuchClientException {
        LOG.debug("Removing client: {}", clientId);
        assertClientExists(clientId);

        clientDao.delete(clientId);
    }

    public List<ClientDetails> listClientDetails() {
        return (List) clientDao.findAll();
    }


    private BaseClientDetails encodeClientSecret(ClientDetails clientDetails) {
        BaseClientDetails cloned = new BaseClientDetails(clientDetails);

        String plain = clientDetails.getClientSecret();
        String encoded = plain != null ? passwordEncoder.encode(plain) : null;
        cloned.setClientSecret(encoded);

        return cloned;
    }

    private void assertClientExists(String clientId) {
        if (! clientDao.exists(clientId)) {
            throw new NoSuchClientException("No such client with id = " + clientId);
        }
    }


    ////////  Accessors  ////////

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDao = clientDAO;
    }
}
