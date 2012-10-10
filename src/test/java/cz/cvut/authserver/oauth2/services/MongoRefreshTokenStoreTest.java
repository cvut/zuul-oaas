package cz.cvut.authserver.oauth2.services;

import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.cvut.authserver.oauth2.Factories.*;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.REFRESH_TOKENS;
import static org.junit.Assert.*;

/**
 * Integration tests for {@link MongoRefreshTokenStore}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration ("classpath:/test-persistence-mongo.xml")
public class MongoRefreshTokenStoreTest {

    private @Autowired MongoTemplate template;
    private MongoRefreshTokenStore tokenStore;


    public @Before void initializeDb() {
        assertFalse("Database should be empty", template.collectionExists(REFRESH_TOKENS));
        tokenStore = new MongoRefreshTokenStore(template);
    }

    public @After void clearDb() {
        template.dropCollection(REFRESH_TOKENS);
    }



    public @Test void read_refresh_token_for_token_value_that_does_not_exist() {

        assertNull(tokenStore.readRefreshToken("tokenThatDoesNotExist"));
    }

    public @Test void store_and_read_refresh_token() {

        OAuth2RefreshToken expectedToken = createRefreshToken("myToken");

        tokenStore.storeRefreshToken(expectedToken, createRandomOAuth2Authentication(true));
        OAuth2RefreshToken actualToken = tokenStore.readRefreshToken("myToken");

        assertEquals(expectedToken.getValue(), actualToken.getValue());
    }

    public @Test void store_and_read_expiring_refresh_token() {

        OAuth2RefreshToken expectedToken = createExpiringRefreshToken("myToken", new Date(42L));

        tokenStore.storeRefreshToken(expectedToken, createRandomOAuth2Authentication(true));
        OAuth2RefreshToken actualToken = tokenStore.readRefreshToken("myToken");

        assertEquals(expectedToken.getValue(), actualToken.getValue());

        ExpiringOAuth2RefreshToken actualExpiringToken = (ExpiringOAuth2RefreshToken) actualToken;
        assertEquals(new Date(42L), actualExpiringToken.getExpiration());
    }



    public @Test void read_authentication_for_token_that_does_not_exist() {

        OAuth2Authentication result = tokenStore.readAuthenticationForRefreshToken(createRefreshToken("tokenThatDoesNotExist"));

        assertNull(result);
    }

    public @Test void read_authentication_for_refresh_token() {

        OAuth2RefreshToken token = createRefreshToken("myToken");
        OAuth2Authentication expectedAuth = createRandomOAuth2Authentication(true);
        tokenStore.storeRefreshToken(token, expectedAuth);

        OAuth2Authentication actualAuth = tokenStore.readAuthenticationForRefreshToken(token);

        assertEquals(expectedAuth, actualAuth);
    }


    public @Test void remove_refresh_token() {

        OAuth2RefreshToken refreshToken = createRefreshToken("myToken");
        tokenStore.storeRefreshToken(refreshToken, createRandomOAuth2Authentication(true));

        assertNotNull(tokenStore.readRefreshToken(refreshToken.getValue()));

        tokenStore.removeRefreshToken(refreshToken);

        assertNull(tokenStore.readRefreshToken(refreshToken.getValue()));

    }

}
