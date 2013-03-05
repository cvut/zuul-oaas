package cz.cvut.authserver.oauth2.dao;

import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static cz.cvut.authserver.oauth2.Factories.*;
import static org.junit.Assert.*;

/**
 * Base class for all {@link RefreshTokenDAO} implementations.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dao-test.xml")
public abstract class AbstractRefreshTokenDAO_IT {

    protected @Autowired RefreshTokenDAO dao;


    public @Test void find_by_non_existing_id() {

        assertNull(dao.findOne("tokenThatDoesNotExist"));
    }

    public @Test void save_and_find_by_id() {

        OAuth2RefreshToken expectedToken = createRefreshToken("myToken");
        OAuth2Authentication expectedAuth = createRandomOAuth2Authentication(true);

        dao.save(new PersistableRefreshToken(expectedToken, expectedAuth));
        PersistableRefreshToken actualToken = dao.findOne("myToken");

        assertEquals(expectedToken.getValue(), actualToken.getValue());
        assertEquals(expectedAuth, actualToken.getAuthentication());
    }

    public @Test void save_and_load_expiring_refresh_token() {

        OAuth2RefreshToken expectedToken = createExpiringRefreshToken("myToken", new Date(42L));

        dao.save(new PersistableRefreshToken(expectedToken, createRandomOAuth2Authentication(true)));
        OAuth2RefreshToken actualToken = dao.findOne("myToken");

        assertEquals(expectedToken.getValue(), actualToken.getValue());

        ExpiringOAuth2RefreshToken actualExpiringToken = (ExpiringOAuth2RefreshToken) actualToken;
        assertEquals(new Date(42L), actualExpiringToken.getExpiration());
    }


    public @Test void remove() {

        OAuth2RefreshToken refreshToken = createRefreshToken("myToken");
        dao.save(new PersistableRefreshToken(refreshToken, createRandomOAuth2Authentication(true)));

        assertNotNull(dao.findOne(refreshToken.getValue()));

        dao.delete(refreshToken);

        assertNull(dao.findOne(refreshToken.getValue()));
    }

}
