package cz.cvut.authserver.oauth2.dao.mongo;

import com.mongodb.WriteResult;
import cz.cvut.authserver.oauth2.dao.ClientDAO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.List;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.client_details.CLIENT_ID;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.client_details.CLIENT_SECRET;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.CLIENT_DETAILS;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoClientDAO implements ClientDAO {

    private static final Class<ClientDetails> ENTITY_CLASS = ClientDetails.class;

    private final MongoOperations mongo;


    public MongoClientDAO(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }


    public List<ClientDetails> findAll() {
        return mongo.findAll(ENTITY_CLASS, CLIENT_DETAILS);
    }

    public ClientDetails findOne(String clientId) {
        return mongo.findById(clientId, ENTITY_CLASS, CLIENT_DETAILS);
    }

    public void save(ClientDetails clientDetails) throws DuplicateKeyException {
        mongo.insert(clientDetails, CLIENT_DETAILS);
    }

    public void update(ClientDetails clientDetails) throws EmptyResultDataAccessException {
        if (! exists(clientDetails.getClientId())) {
            throw new EmptyResultDataAccessException("No such client with clientId = " + clientDetails.getClientId(), 1);
        }
        mongo.save(clientDetails, CLIENT_DETAILS);
    }

    public void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException {
        WriteResult result = mongo.updateFirst(
                query(where(CLIENT_ID).is(clientId)),
                Update.update(CLIENT_SECRET, secret),
                CLIENT_DETAILS);

        if (result.getN() == 0) {
            throw new EmptyResultDataAccessException("No such client with clientId = " + clientId, 1);
        }
    }

    public void delete(String clientId) {
        mongo.remove(query(where(CLIENT_ID).is(clientId)), CLIENT_DETAILS);
    }

    public boolean exists(String clientId) {
        return mongo.findById(clientId, ENTITY_CLASS, CLIENT_DETAILS) != null;
    }

}
