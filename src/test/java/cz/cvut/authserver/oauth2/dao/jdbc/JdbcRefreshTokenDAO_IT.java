package cz.cvut.authserver.oauth2.dao.jdbc;

import cz.cvut.authserver.oauth2.dao.AbstractRefreshTokenDAO_IT;
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
public class JdbcRefreshTokenDAO_IT extends AbstractRefreshTokenDAO_IT {

    public @After void cleanDb() {
        dao.deleteAll();
    }
}
