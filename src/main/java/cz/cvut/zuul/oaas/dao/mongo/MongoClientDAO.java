package cz.cvut.zuul.oaas.dao.mongo;

import com.mongodb.WriteResult;
import cz.cvut.zuul.oaas.dao.ClientDAO;
import cz.cvut.zuul.oaas.models.Client;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoClientDAO extends AbstractMongoGenericDAO<Client, String> implements ClientDAO {


    public void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException {
        WriteResult result = mongo().updateFirst(
                query(where("_id").is(clientId)),
                update("clientSecret", secret),
                entityClass());

        if (result.getN() == 0) {
            throw new EmptyResultDataAccessException("No such client with clientId = " + clientId, 1);
        }
    }
}
