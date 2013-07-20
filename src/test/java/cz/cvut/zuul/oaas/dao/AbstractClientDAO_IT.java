package cz.cvut.zuul.oaas.dao;

import cz.cvut.zuul.oaas.models.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.net.URI;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.cvut.zuul.oaas.Factories.createEmptyClient;
import static cz.cvut.zuul.oaas.Factories.createRandomClient;
import static cz.cvut.zuul.oaas.TestUtils.assertEachEquals;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Base integration tests for all {@link ClientDAO} implementations.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dao-test.xml")
public abstract class AbstractClientDAO_IT {

    protected @Autowired ClientDAO dao;


    public @Test void find_client_by_non_existing_id() {

        assertNull(dao.findOne("nonExistingClientId"));
    }

    public @Test void save_and_find_client_with_no_details() {

        Client expected = createEmptyClient("emptyClientId");

        dao.save(expected);
        Client actual = dao.findOne(expected.getClientId());

        assertEquals(expected.getClientId(), actual.getClientId());
        assertNull(actual.getClientSecret());
        assertEquals(0, actual.getScope().size());
        assertEquals(0, actual.getAuthorizedGrantTypes().size());
        assertEquals(0, actual.getRegisteredRedirectUri().size());
        assertEquals(0, actual.getAuthorities().size());
        assertEquals(null, actual.getAccessTokenValiditySeconds());
        assertEquals(null, actual.getRefreshTokenValiditySeconds());
        assertEquals(null, actual.getProductName());
        assertEquals(false, actual.isLocked());
    }

    public @Test void save_and_find_client_with_details() {

        Client expected = createRandomClient();
        expected.setLocked(true);  //false is default value so we have to test true

        dao.save(expected);
        Client actual = dao.findOne(expected.getClientId());

        assertEquals(expected.getClientId(), actual.getClientId());
        assertNotNull(actual.getClientSecret());
        assertEachEquals(expected.getScope(), actual.getScope());
        assertEachEquals(expected.getResourceIds(), actual.getResourceIds());
        assertEachEquals(expected.getAuthorizedGrantTypes(), actual.getAuthorizedGrantTypes());
        assertEachEquals(expected.getRegisteredRedirectUri(), actual.getRegisteredRedirectUri());
        assertEachEquals(expected.getAuthorities(), actual.getAuthorities());
        assertEquals(expected.getAccessTokenValiditySeconds(), actual.getAccessTokenValiditySeconds());
        assertEquals(expected.getRefreshTokenValiditySeconds(), actual.getRefreshTokenValiditySeconds());
        assertEquals(expected.getProductName(), actual.getProductName());
        assertEquals(expected.isLocked(), actual.isLocked());
    }


    public @Test void update_redirect_uri() {

        Client expected = createRandomClient();
        dao.save(expected);

        URI[] redirectURI = {URI.create("http://localhost:8080"), URI.create("http://localhost:9090")};
        expected.setRegisteredRedirectUri(asList(redirectURI));
        dao.save(expected);

        Client actual = dao.findOne(expected.getClientId());

        assertEquals(expected.getClientId(), actual.getClientId());
        assertEachEquals(expected.getRegisteredRedirectUri(), actual.getRegisteredRedirectUri());
    }


    public @Test void remove_non_existing_client() {

        dao.delete("nonExistentClientId");
    }

    public void remove() {

        Client client = createRandomClient();
        dao.save(client);

        assertNotNull(dao.findOne(client.getClientId()));

        dao.delete(client.getClientId());

        assertNull(dao.findOne(client.getClientId()));
    }


    @Test(expected = EmptyResultDataAccessException.class)
    public void update_secret_for_non_existing_client() {

        dao.updateClientSecret("NonExistentId", "foo");
    }

    public @Test void update_secret() {

        Client expected = createRandomClient();
        expected.setClientSecret("foo");
        dao.save(expected);

        dao.updateClientSecret(expected.getClientId(), "bar");

        Client actual = dao.findOne(expected.getClientId());

        assertEquals(expected.getClientId(), actual.getClientId());
        assertFalse("Client secrets should not be same", expected.getClientSecret().equals(actual.getClientSecret()));
    }

}
