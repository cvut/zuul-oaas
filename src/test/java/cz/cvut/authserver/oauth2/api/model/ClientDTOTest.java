package cz.cvut.authserver.oauth2.api.model;

import com.google.common.collect.Sets;
import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static cz.cvut.authserver.oauth2.Factories.createAllAuthorizationGrants;
import static cz.cvut.authserver.oauth2.Factories.createInvalidAuthorizationGrants;
import static cz.cvut.authserver.oauth2.TestUtils.assertInvalidProperty;
import static cz.cvut.authserver.oauth2.TestUtils.assertValidProperty;
import static java.util.Arrays.asList;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ClientDTOTest {
//TODO test JSON (de)serialization

    private ClientDTO client;


    public @Before void initialize() {
        client = new ClientDTO();
    }


    public @Test void validate_empty_scope() {
        assertInvalidProperty(client, "scope");
    }

    public @Test void validate_scope_with_illegal_chars() {
        client.setScope(asList("fooooo", "d1r%ty' s*tr$n6+= @}"));
        assertInvalidProperty(client, "scope");
    }

    public @Test void validate_scope_with_exceeded_length() {
        client.setScope(asList(RandomStringUtils.randomAscii(257)));
        assertInvalidProperty(client, "scope");
    }

    public @Test void validate_valid_scope() {
        client.setScope(asList("poignant", "chun_ky.B4c-0n"));
        assertValidProperty(client, "scope");
    }


    public @Test void validate_empty_authorized_grant() {
        assertInvalidProperty(client, "authorizedGrantTypes");
    }

    public @Test void validate_invalid_authorized_grant() {
        client.setAuthorizedGrantTypes(createInvalidAuthorizationGrants());
        assertInvalidProperty(client, "authorizedGrantTypes");
    }

    public @Test void validate_valid_authorized_grants() {
        client.setAuthorizedGrantTypes(createAllAuthorizationGrants());
        assertValidProperty(client, "authorizedGrantTypes");
    }


    public @Test void validate_empty_redirect_uri() {
        assertValidProperty(client, "registeredRedirectUri");
    }

    public @Test void validate_invalid_redirect_uri() {
        client.setRegisteredRedirectUri(Sets.newHashSet("foo", "baaaar"));

        assertInvalidProperty(client, "registeredRedirectUri");
    }

    public @Test void validate_relative_redirect_uri() {
        client.setRegisteredRedirectUri(Sets.newHashSet("/relative/url", "../another/relative"));

        assertInvalidProperty(client, "registeredRedirectUri");
    }

    public @Test void validate_redirect_uri_with_fragment() {
        client.setRegisteredRedirectUri(Sets.newHashSet("http://cvut.cz/cool#fragment"));

        assertInvalidProperty(client, "registeredRedirectUri");
    }

    public @Test void validate_redirect_uri_with_exceeded_length() {
        client.setRegisteredRedirectUri(Sets.newHashSet("http://cvut.cz/" + RandomStringUtils.random(242)));

        assertInvalidProperty(client, "registeredRedirectUri");
    }

    public @Test void validate_valid_redirect_uri() {
        client.setRegisteredRedirectUri(Sets.newHashSet("http://cvut.cz/", "urn:cvut:cool"));

        assertValidProperty(client, "registeredRedirectUri");
    }


    @Ignore
    public @Test void should_not_accept_empty_redirect_uris_when_grant_auth_code() {
        //TODO
    }

    @Ignore
    public @Test void should_accept_empty_redirect_uris_when_not_grant_auth_code() {
        //TODO
    }

}
