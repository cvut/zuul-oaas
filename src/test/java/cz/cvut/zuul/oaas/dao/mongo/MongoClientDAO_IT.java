package cz.cvut.zuul.oaas.dao.mongo;

import cz.cvut.zuul.oaas.dao.AbstractClientDAO_IT;
import cz.cvut.zuul.oaas.models.Client;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertFalse;

/**
 * Integration tests for {@link cz.cvut.zuul.oaas.dao.ClientDAO} implementations.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@ActiveProfiles("mongo")
@IfProfileValue(name="it-profile", values={"all", "mongo"})
public class MongoClientDAO_IT extends AbstractClientDAO_IT {

    private @Autowired MongoTemplate template;


    public @Before void initializeDb() {
        assertFalse("Database should be empty", template.collectionExists(Client.class));
    }

    public @After void clearDb() {
        template.dropCollection(Client.class);
    }

}
