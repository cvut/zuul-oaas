package cz.cvut.authserver.oauth2.dao.jdbc;

import cz.cvut.authserver.oauth2.dao.AbstractResourceDAO_IT;
import org.junit.After;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Transactional
@ActiveProfiles("jdbc")
@IfProfileValue(name="it-profile", values={"all", "jdbc"})
public class JdbcResourceDAO_IT extends AbstractResourceDAO_IT {

    public @After void cleanDb() {
        dao.deleteAll();
    }
}
