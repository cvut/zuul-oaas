package cz.cvut.authserver.oauth2.dao;

import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.cvut.authserver.oauth2.Factories.*;
import static cz.cvut.authserver.oauth2.TestUtils.assertEachEquals;
import static org.junit.Assert.*;

/**
 * Base integration tests for all {@link AccessTokenDAO} implementations.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dao-test.xml")
public abstract class AbstractAccessTokenDAO_IT {

    protected @Autowired AccessTokenDAO dao;


    public @Test void find_by_non_existing_id() {

        assertNull(dao.findOne("tokenThatDoesNotExist"));
    }

    public @Test void save_and_find_by_id() {

        OAuth2AccessToken expectedToken = createEmptyAccessToken("emptyToken");
        OAuth2Authentication expectedAuth = createEmptyOAuth2Authentication("emptyClientId");

        dao.save(new PersistableAccessToken(expectedToken, expectedAuth));
        OAuth2AccessToken actualToken = dao.findOne("emptyToken");

        assertEquals("emptyToken", actualToken.getValue());
        assertEquals(expectedToken.getAdditionalInformation(), actualToken.getAdditionalInformation());
        assertEquals(expectedToken.getExpiration(), actualToken.getExpiration());
        assertEquals(expectedToken.getRefreshToken(), actualToken.getRefreshToken());
        assertEachEquals(expectedToken.getScope(), actualToken.getScope());
        assertEquals(expectedToken.getTokenType(), actualToken.getTokenType());
    }

    public @Test void save_and_load_token_with_client_only_authentication() {

        OAuth2AccessToken expectedToken = createEmptyAccessToken("emptyToken");
        OAuth2Authentication expectedAuth = createRandomOAuth2Authentication(true);

        dao.save(new PersistableAccessToken(expectedToken, expectedAuth));
        OAuth2Authentication actualAuth = dao.findOne("emptyToken").getAuthentication();

        assertTrue(actualAuth.isClientOnly());
        assertEachEquals(expectedAuth.getAuthorities(), actualAuth.getAuthorities());
        assertEquals(expectedAuth.getAuthorizationRequest(), actualAuth.getAuthorizationRequest());
        assertEquals(expectedAuth.getCredentials(), actualAuth.getCredentials());
        assertEquals(expectedAuth.getName(), actualAuth.getName());
        assertEquals(expectedAuth.getPrincipal(), actualAuth.getPrincipal());
    }

    public @Test void save_and_load_token_with_client_and_user_authentication() {

        Authentication expectedUserAuth = createUserAuthentication("myName", false);
        OAuth2Authentication authentication = new OAuth2Authentication(
                createRandomAuthorizationRequest(),
                expectedUserAuth);
        dao.save(new PersistableAccessToken(createEmptyAccessToken("emptyToken"), authentication));

        OAuth2Authentication auth = dao.findOne("emptyToken").getAuthentication();
        Authentication actualUserAuth = auth.getUserAuthentication();

        assertFalse(auth.isClientOnly());
        assertEachEquals(expectedUserAuth.getAuthorities(), actualUserAuth.getAuthorities());
        assertEquals(expectedUserAuth.getCredentials(), actualUserAuth.getCredentials());
        assertEquals(expectedUserAuth.getName(), actualUserAuth.getName());
        assertEquals(expectedUserAuth.getPrincipal(), actualUserAuth.getPrincipal());
    }


    public @Test void find_by_authentication_that_does_not_exist() {

        assertNull(dao.findByAuthentication(createEmptyOAuth2Authentication("doesNotExist")));
    }

    public @Test void find_by_authentication() {

        OAuth2AccessToken expectedToken = createRandomAccessToken();
        OAuth2Authentication authentication = createRandomOAuth2Authentication(true);
        dao.save(new PersistableAccessToken(expectedToken, authentication));

        OAuth2AccessToken actualToken = dao.findByAuthentication(authentication);

        assertEquals(expectedToken, actualToken);
    }


    public @Test void remove() {

        OAuth2AccessToken accessToken = createRandomAccessToken();
        dao.save(new PersistableAccessToken(accessToken, createEmptyOAuth2Authentication("dummy")));

        assertNotNull(dao.findOne(accessToken.getValue()));

        dao.remove(accessToken);

        assertNull(dao.findOne(accessToken.getValue()));
    }

    public @Test void remove_by_refresh_token() {

        OAuth2AccessToken accessToken = createEmptyAccessTokenWithRefreshToken("access", "refresh");
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        dao.save(new PersistableAccessToken(accessToken, createRandomOAuth2Authentication(true)));

        assertNotNull(dao.findOne("access"));

        dao.removeByRefreshToken(refreshToken);

        assertNull(dao.findOne("access"));
    }


    public @Test void find_by_client_id() {

        for (int i = 0; i < 2; i++) {
            dao.save(new PersistableAccessToken(createRandomAccessToken(), createEmptyOAuth2Authentication("emptyClientId")));
        }

        assertEquals(2, dao.findByClientId("emptyClientId").size());
    }

    public @Test void find_by_user_name() {

        for (int i = 0; i < 2; i++) {
            OAuth2Authentication authentication = new OAuth2Authentication(
                    createRandomAuthorizationRequest(),
                    createUserAuthentication("myName", false));
            dao.save(new PersistableAccessToken(createRandomAccessToken(), authentication));
        }

        assertEquals(2, dao.findByUserName("myName").size());
    }

}
