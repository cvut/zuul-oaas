package cz.cvut.authserver.oauth2.services;

import com.mongodb.WriteResult;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.client_details.*;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.CLIENT_DETAILS;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 * MongoDB implementation of the Client details and registration service.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(MongoClientDetailsService.class);

    private final MongoOperations mongo;
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();


    public MongoClientDetailsService(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }


    public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {
        ClientDetails result = mongo.findById(clientId, ClientDetails.class, CLIENT_DETAILS);
        if (result == null) {
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
        return result;
    }

    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        try {
            LOG.debug("Adding client: {}", clientDetails.getClientId());
            mongo.insert(encodeClientSecret(clientDetails), CLIENT_DETAILS);

        } catch (DuplicateKeyException ex) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId());
        }
    }

    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        LOG.debug("Updating client: {}", clientDetails.getClientId());

        assertClientExists(clientDetails.getClientId());
        mongo.save(clientDetails, CLIENT_DETAILS);
    }

    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        LOG.debug("Updating secret for client: {}", clientId);

        WriteResult result = mongo.updateFirst(
                query(where(CLIENT_ID).is(clientId)),
                update(CLIENT_SECRET, passwordEncoder.encode(secret)),
                CLIENT_DETAILS);
        if (result.getN() == 0) {
            throw new NoSuchClientException("No such client with clientId = " + clientId);
        }
    }

    public void removeClientDetails(String clientId) throws NoSuchClientException {
        LOG.debug("Removing client: {}", clientId);

        assertClientExists(clientId);
        mongo.remove(query(where(CLIENT_ID).is(clientId)), CLIENT_DETAILS);
    }

    public List<ClientDetails> listClientDetails() {
        return mongo.findAll(ClientDetails.class, CLIENT_DETAILS);
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }



    private void assertClientExists(String clientId) {
        ClientDetails client = mongo.findById(clientId, ClientDetails.class, CLIENT_DETAILS);

        if (client == null) {
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
    }

    private BaseClientDetails encodeClientSecret(ClientDetails clientDetails) {
        BaseClientDetails cloned = new BaseClientDetails(clientDetails);

        String plain = clientDetails.getClientSecret();
        String encoded = plain != null ? passwordEncoder.encode(plain) : null;
        cloned.setClientSecret(encoded);

        return cloned;
    }
}
