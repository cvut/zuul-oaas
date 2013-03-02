package cz.cvut.authserver.oauth2.dao.mongo;

import com.mongodb.WriteResult;
import cz.cvut.authserver.oauth2.dao.ClientDAO;
import cz.cvut.authserver.oauth2.models.Client;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoClientDAO implements ClientDAO {

    private static final Class<Client> ENTITY_CLASS = Client.class;

    private final MongoOperations mongo;


    public MongoClientDAO(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }


    public List<Client> findAll() {
        return mongo.findAll(ENTITY_CLASS);
    }

    public Client findOne(String clientId) {
        return mongo.findById(clientId, ENTITY_CLASS);
    }

    public void save(Client client) throws DuplicateKeyException {
        mongo.insert(client);
    }

    public void update(Client client) throws EmptyResultDataAccessException {
        if (! exists(client.getClientId())) {
            throw new EmptyResultDataAccessException("No such client with clientId = " + client.getClientId(), 1);
        }
        mongo.save(client);
    }

    public void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException {
        WriteResult result = mongo.updateFirst(
                query(where("_id").is(clientId)),
                Update.update("clientSecret", secret),
                ENTITY_CLASS);

        if (result.getN() == 0) {
            throw new EmptyResultDataAccessException("No such client with clientId = " + clientId, 1);
        }
    }

    public void delete(String clientId) {
        mongo.remove(query(where("_id").is(clientId)), ENTITY_CLASS);
    }

    public boolean exists(String clientId) {
        return mongo.findById(clientId, ENTITY_CLASS) != null;
    }

}
