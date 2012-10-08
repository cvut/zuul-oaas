package cz.cvut.authserver.oauth2.services;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.cvut.authserver.oauth2.Factories.*;
import static cz.cvut.authserver.oauth2.mongo.MongoDbConstants.collections.CLIENT_DETAILS;
import static org.junit.Assert.*;

/**
 * Integration tests for {@link MongoClientDetailsService}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-persistence-mongo.xml")
public class MongoClientDetailsServiceTest {

    private @Autowired MongoTemplate template;

    private MongoClientDetailsService service;


    public @Before void initializeDb() {
        assertFalse("Database should be empty", template.collectionExists(CLIENT_DETAILS));
        service = new MongoClientDetailsService(template);
    }

    public @After void clearDb() {
        template.dropCollection(CLIENT_DETAILS);
    }



    @Test(expected = NoSuchClientException.class)
    public void load_client_for_non_existing_client_id() {

        service.loadClientByClientId("nonExistingClientId");
    }

    public @Test void save_and_load_client_with_no_details() {

        ClientDetails expected = createEmptyClientDetails("clientIdWithNoDetails");

        service.addClientDetails(expected);
        ClientDetails actual = service.loadClientByClientId("clientIdWithNoDetails");

        assertEquals("clientIdWithNoDetails", actual.getClientId());
        assertFalse(actual.isSecretRequired());
        assertNull(actual.getClientSecret());
        assertFalse(actual.isScoped());
        assertEquals(0, actual.getScope().size());
        assertEquals(2, actual.getAuthorizedGrantTypes().size());
        assertNull(actual.getRegisteredRedirectUri());
        assertEquals(0, actual.getAuthorities().size());
        assertEquals(null, actual.getAccessTokenValiditySeconds());
        assertEquals(null, actual.getAccessTokenValiditySeconds());
    }

    public @Test void save_and_load_client_with_details() {

        ClientDetails expected = createRandomClientDetails("clientIdWithDetails");

        service.addClientDetails(expected);
        ClientDetails actual = service.loadClientByClientId("clientIdWithDetails");

        assertEquals("clientIdWithDetails", actual.getClientId());
        assertTrue(actual.isSecretRequired());
        assertEquals(expected.getClientSecret(), actual.getClientSecret());
        assertTrue(actual.isScoped());
        assertEquals(expected.getScope(), actual.getScope());
        assertEquals(expected.getResourceIds(), actual.getResourceIds());
        assertEquals(expected.getAuthorizedGrantTypes(), actual.getAuthorizedGrantTypes());
        assertEquals(expected.getRegisteredRedirectUri(), actual.getRegisteredRedirectUri());
        assertEquals(new HashSet<>(expected.getAuthorities()), new HashSet<>(actual.getAuthorities()));  //relativize order
        assertEquals(expected.getAccessTokenValiditySeconds(), actual.getAccessTokenValiditySeconds());
        assertEquals(expected.getRefreshTokenValiditySeconds(), actual.getRefreshTokenValiditySeconds());
    }

    @Test(expected = ClientAlreadyExistsException.class)
    public void insert_duplicate_client() {

        ClientDetails client = createRandomClientDetails();

        service.addClientDetails(client);
        service.addClientDetails(client);
    }


    @Test(expected = NoSuchClientException.class)
    public void update_client_secret_for_non_existing_client_id() {

        service.updateClientSecret("NonExistentId", "dummy");
    }

    public @Test void update_client_secret() {

        ClientDetails expected = createRandomClientDetails("clientIdWithRandomDetails");
        service.addClientDetails(expected);

        service.setPasswordEncoder(new PasswordEncoder() {
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return true;
            }
            public String encode(CharSequence rawPassword) {
                return "BAR";
            }
        });
        service.updateClientSecret(expected.getClientId(), "foo");

        ClientDetails actual = service.loadClientByClientId(expected.getClientId());

        assertEquals(expected.getClientId(), actual.getClientId());
        assertEquals("BAR", actual.getClientSecret());
    }


    @Test(expected = NoSuchClientException.class)
    public void update_non_existent_client() {

        ClientDetails client = createRandomClientDetails("nonExistentClientId");
        service.updateClientDetails(client);
    }

    public @Test void update_client_redirect_uri() {

        BaseClientDetails expected = (BaseClientDetails) createRandomClientDetails("clientIdWithRandomDetails");
        service.addClientDetails(expected);

        String[] redirectURI = {"http://localhost:8080", "http://localhost:9090"};
        expected.setRegisteredRedirectUri(new HashSet<>(Arrays.asList(redirectURI)));
        service.updateClientDetails(expected);

        ClientDetails actual = service.loadClientByClientId(expected.getClientId());

        assertEquals(expected.getClientId(), actual.getClientId());
        assertEquals(expected.getRegisteredRedirectUri(), actual.getRegisteredRedirectUri());
    }


    @Test(expected = NoSuchClientException.class)
    public void remove_non_existent_client() {

        service.removeClientDetails("nonExistentClientId");
    }

    @Test(expected = NoSuchClientException.class)
    public void remove_client() {

        ClientDetails client = createRandomClientDetails("clientIdWithRandomDetails");
        service.addClientDetails(client);

        assertNotNull(service.loadClientByClientId(client.getClientId()));

        service.removeClientDetails(client.getClientId());

        service.loadClientByClientId(client.getClientId());
    }


    public @Test void find_clients() {

        for (int i = 0; i < 3; i++) {
            service.addClientDetails(createRandomClientDetails());
        }

        int count = service.listClientDetails().size();

        assertEquals(3, count);
    }

}
