package cz.cvut.authserver.oauth2.services;

import cz.cvut.authserver.oauth2.dao.mongo.MongoAccessTokenDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;

import static cz.cvut.authserver.oauth2.Factories.*;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.ACCESS_TOKENS;
import static org.junit.Assert.*;

/**
 * Integration tests for {@link cz.cvut.authserver.oauth2.dao.mongo.MongoAccessTokenDAO}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@IfProfileValue(name="it-profile", values={"all", "mongo"})
@ContextConfiguration ("classpath:/test-persistence-mongo.xml")
public class MongoAccessTokenStoreTest {

    private @Autowired MongoTemplate template;
    
    private PersistentTokenStore tokenStore;


    public @Before void initializeDb() {
        assertFalse("Database should be empty", template.collectionExists(ACCESS_TOKENS));

        tokenStore = new PersistentTokenStore();
        tokenStore.setAccessTokenDAO(new MongoAccessTokenDAO(template));
    }

    public @After void clearDb() {
        template.dropCollection(ACCESS_TOKENS);
    }



    public @Test void load_access_token_for_token_that_does_not_exist() {

        assertNull(tokenStore.readAccessToken("tokenThatDoesNotExist"));
    }

    public @Test void store_access_token_with_authentication_and_read_access_token() {

        OAuth2AccessToken expectedToken = createEmptyAccessToken("emptyToken");
        OAuth2Authentication expectedAuth = createEmptyOAuth2Authentication("emptyClientId");

        tokenStore.storeAccessToken(expectedToken, expectedAuth);
        OAuth2AccessToken actualToken = tokenStore.readAccessToken("emptyToken");

        assertEquals("emptyToken", actualToken.getValue());
        assertEquals(expectedToken.getAdditionalInformation(), actualToken.getAdditionalInformation());
        assertEquals(expectedToken.getExpiration(), actualToken.getExpiration());
        assertEquals(expectedToken.getRefreshToken(), actualToken.getRefreshToken());
        assertEquals(expectedToken.getScope(), actualToken.getScope());
        assertEquals(expectedToken.getTokenType(), actualToken.getTokenType());
    }

    public @Test void load_access_token_for_authentication_that_does_not_exist() {

        assertNull(tokenStore.getAccessToken(createEmptyOAuth2Authentication("doesNotExist")));
    }
    
    public @Test void store_access_token_with_authentication_and_read_authentication() {
        
        OAuth2AccessToken expectedToken = createEmptyAccessToken("emptyToken");
        OAuth2Authentication expectedAuth = createRandomOAuth2Authentication(true);
        
        tokenStore.storeAccessToken(expectedToken, expectedAuth);
        OAuth2Authentication actualAuth = tokenStore.readAuthentication("emptyToken");

        assertTrue(actualAuth.isClientOnly());
        assertEquals(new HashSet<>(expectedAuth.getAuthorities()), new HashSet<>(actualAuth.getAuthorities()));  //relativize order
        assertEquals(expectedAuth.getAuthorizationRequest(), actualAuth.getAuthorizationRequest());
        assertEquals(expectedAuth.getCredentials(), actualAuth.getCredentials());
        assertEquals(expectedAuth.getName(), actualAuth.getName());
        assertEquals(expectedAuth.getPrincipal(), actualAuth.getPrincipal());
    }


    public @Test void load_access_token_for_authentication() {

        OAuth2AccessToken expectedToken = createRandomAccessToken();
        OAuth2Authentication authentication = createRandomOAuth2Authentication(true);
        tokenStore.storeAccessToken(expectedToken, authentication);

        OAuth2AccessToken actualToken = tokenStore.getAccessToken(authentication);

        assertEquals(expectedToken, actualToken);
    }


    public @Test void remove_access_token() {

        OAuth2AccessToken accessToken = createRandomAccessToken();
        tokenStore.storeAccessToken(accessToken, createEmptyOAuth2Authentication("dummy"));

        assertNotNull(tokenStore.readAccessToken(accessToken.getValue()));

        tokenStore.removeAccessToken(accessToken);

        assertNull(tokenStore.readAccessToken(accessToken.getValue()));
    }


    public @Test void remove_access_token_using_refresh_token() {

        OAuth2AccessToken accessToken = createEmptyAccessTokenWithRefreshToken("access", "refresh");
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        tokenStore.storeAccessToken(accessToken, createRandomOAuth2Authentication(true));

        assertNotNull(tokenStore.readAccessToken("access"));

        tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);

        assertNull(tokenStore.readAccessToken("access"));
    }

    
    public @Test void read_authentication_for_token_that_does_not_exist() {

        assertNull(tokenStore.readAuthentication("tokenThatDoesNotExist"));
    }

    public @Test void read_client_only_authentication() {

        OAuth2Authentication expectedAuth = createRandomOAuth2Authentication(true);
        tokenStore.storeAccessToken(createEmptyAccessToken("emptyToken"), expectedAuth);

        OAuth2Authentication actualAuth = tokenStore.readAuthentication("emptyToken");

        assertTrue(actualAuth.isClientOnly());
        assertEquals(expectedAuth.getAuthorities(), actualAuth.getAuthorities());
        assertEquals(new HashSet<>(expectedAuth.getAuthorities()), new HashSet<>(actualAuth.getAuthorities()));  //relativize order
        assertEquals(expectedAuth.getCredentials(), actualAuth.getCredentials());
        assertEquals(expectedAuth.getName(), actualAuth.getName());
        assertEquals(expectedAuth.getPrincipal(), actualAuth.getPrincipal());
    }

    public @Test void read_client_and_user_authentication() {

        Authentication expectedUserAuth = createUserAuthentication("myName", false);
        OAuth2Authentication authentication = new OAuth2Authentication(
                createRandomAuthorizationRequest(),
                expectedUserAuth);
        tokenStore.storeAccessToken(createEmptyAccessToken("emptyToken"), authentication);

        OAuth2Authentication auth = tokenStore.readAuthentication("emptyToken");
        Authentication actualUserAuth = auth.getUserAuthentication();

        assertFalse(auth.isClientOnly());
        assertEquals(actualUserAuth.getAuthorities(), actualUserAuth.getAuthorities());
        assertEquals(actualUserAuth.getCredentials(), actualUserAuth.getCredentials());
        assertEquals(actualUserAuth.getName(), actualUserAuth.getName());
        assertEquals(actualUserAuth.getPrincipal(), actualUserAuth.getPrincipal());
    }


    public @Test void find_access_tokens_by_client_id() {

        for (int i = 0; i < 2; i++) {
            tokenStore.storeAccessToken(createRandomAccessToken(), createEmptyOAuth2Authentication("emptyClientId"));
        }

        assertEquals(2, tokenStore.findTokensByClientId("emptyClientId").size());
    }


    public @Test void find_access_tokens_by_user_name() {

        for (int i = 0; i < 2; i++) {
            OAuth2Authentication authentication = new OAuth2Authentication(
                    createRandomAuthorizationRequest(),
                    createUserAuthentication("myName", false));
            tokenStore.storeAccessToken(createRandomAccessToken(), authentication);
        }

        assertEquals(2, tokenStore.findTokensByUserName("myName").size());
    }

}
