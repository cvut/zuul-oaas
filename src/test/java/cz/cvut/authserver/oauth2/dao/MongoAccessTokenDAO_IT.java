package cz.cvut.authserver.oauth2.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.ACCESS_TOKENS;
import static org.junit.Assert.assertFalse;

/**
 * Integration tests for {@link cz.cvut.authserver.oauth2.dao.mongo.MongoAccessTokenDAO}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@IfProfileValue(name="it-profile", values={"all", "mongo"})
@ContextConfiguration("classpath:dao-test.xml")
public class MongoAccessTokenDAO_IT extends AbstractAccessTokenDAO_IT {

    private @Autowired MongoTemplate template;


    public @Before void initializeDb() {
        template.dropCollection(ACCESS_TOKENS);
        assertFalse("Database should be empty", template.collectionExists(ACCESS_TOKENS));
    }

    public @After void clearDb() {
        //template.dropCollection(ACCESS_TOKENS); TODO why not working correctly?
    }

}
