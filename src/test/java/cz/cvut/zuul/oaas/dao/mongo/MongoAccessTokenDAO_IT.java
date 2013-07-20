package cz.cvut.zuul.oaas.dao.mongo;

import cz.cvut.zuul.oaas.dao.AbstractAccessTokenDAO_IT;
import cz.cvut.zuul.oaas.models.PersistableAccessToken;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertFalse;

/**
 * Integration tests for {@link cz.cvut.zuul.oaas.dao.mongo.MongoAccessTokenDAO}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@ActiveProfiles("mongo")
@IfProfileValue(name="it-profile", values={"all", "mongo"})
public class MongoAccessTokenDAO_IT extends AbstractAccessTokenDAO_IT {

    private @Autowired MongoTemplate template;


    public @Before void initializeDb() {
        template.dropCollection(PersistableAccessToken.class);
        assertFalse("Database should be empty", template.collectionExists(PersistableAccessToken.class));
    }

    public @After void clearDb() {
        //template.dropCollection(PersistableAccessToken.class); TODO why not working correctly?
    }

}
