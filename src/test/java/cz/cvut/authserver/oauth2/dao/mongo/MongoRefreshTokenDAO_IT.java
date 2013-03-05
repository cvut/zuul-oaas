package cz.cvut.authserver.oauth2.dao.mongo;

import cz.cvut.authserver.oauth2.dao.AbstractRefreshTokenDAO_IT;
import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertFalse;

/**
 * Integration tests for {@link cz.cvut.authserver.oauth2.dao.mongo.MongoRefreshTokenDAO}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@ActiveProfiles("mongo")
@IfProfileValue(name="it-profile", values={"all", "mongo"})
public class MongoRefreshTokenDAO_IT extends AbstractRefreshTokenDAO_IT {

    private @Autowired MongoTemplate template;


    public @Before void initializeDb() {
        assertFalse("Database should be empty", template.collectionExists(PersistableRefreshToken.class));
    }

    public @After void clearDb() {
        template.dropCollection(PersistableRefreshToken.class);
    }
}
