package cz.cvut.authserver.oauth2.dao.jdbc;

import cz.cvut.authserver.oauth2.dao.AbstractClientDAO_IT;
import org.junit.After;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jimmy
 */
@Transactional
@ActiveProfiles("jdbc")
@IfProfileValue(name="it-profile", values={"all", "jdbc"})
public class JdbcClientDAO_IT extends AbstractClientDAO_IT {

    public @After void cleanDb() {
        dao.deleteAll();
    }
}
