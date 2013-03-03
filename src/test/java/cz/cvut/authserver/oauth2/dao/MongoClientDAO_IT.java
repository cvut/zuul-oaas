package cz.cvut.authserver.oauth2.dao;

import cz.cvut.authserver.oauth2.models.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;

/**
 * Integration tests for {@link ClientDAO} implementations.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@IfProfileValue(name="it-profile", values={"all", "mongo"})
@ContextConfiguration("classpath:dao-test.xml")
public class MongoClientDAO_IT extends AbstractClientDAO_IT {

    private @Autowired MongoTemplate template;


    public @Before void initializeDb() {
        assertFalse("Database should be empty", template.collectionExists(Client.class));
    }

    public @After void clearDb() {
        template.dropCollection(Client.class);
    }

}
