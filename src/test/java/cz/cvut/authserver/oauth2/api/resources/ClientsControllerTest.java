package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.Factories;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.test.web.server.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ClientsControllerTest {

    private static final String API_VERSION = "v1";
    private static final String BASE_URI = "/" + API_VERSION + "/clients/";
    private static final String MIME_TYPE_JSON = "application/json;charset=UTF-8";
    // JSON attributes
    public static final String CLIENT_ID = "client_id",
            CLIENT_SECRET = "client_secret",
            RESOURCE_IDS = "resource_ids",
            GRANT_TYPES = "authorized_grant_types",
            REDIRECT_URI = "redirect_uri",
            ACCESS_TOKEN_VALIDITY = "access_token_validity",
            REFRESH_TOKEN_VALIDITY = "refresh_token_validity";
    private @Mock
    ClientRegistrationService clientRegistrationService;
    private @Mock
    ClientDetailsService clientDetailsService;
    private @InjectMocks
    ClientsController clientsController;
    private MockMvc mock;

    @Before
    public void buildMocks() {
        mock = standaloneSetup(clientsController).build();
    }

    @Test
    public void get_client_details_for_non_existing_client_id() throws Exception {
        doThrow(NoSuchClientException.class).when(clientDetailsService).loadClientByClientId("666");

        mock.perform(get(BASE_URI + 666).accept(APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void get_client_details() throws Exception {
        ClientDetails expected = Factories.createRandomClientDetails("42");
        doReturn(expected).when(clientDetailsService).loadClientByClientId("42");

        mock.perform(get(BASE_URI + 42)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(MIME_TYPE_JSON))
                .andExpect(jsonPath(CLIENT_ID, equalTo("42")))
                .andExpect(jsonPath(CLIENT_SECRET, equalTo(expected.getClientSecret())))
                .andExpect(jsonPath(RESOURCE_IDS, hasItems(expected.getResourceIds().toArray())))
                .andExpect(jsonPath(GRANT_TYPES, hasItems(expected.getAuthorizedGrantTypes().toArray())))
                .andExpect(jsonPath(REDIRECT_URI, hasItems(expected.getRegisteredRedirectUri().toArray())))
                .andExpect(jsonPath(ACCESS_TOKEN_VALIDITY, equalTo(expected.getAccessTokenValiditySeconds())))
                .andExpect(jsonPath(REFRESH_TOKEN_VALIDITY, equalTo(expected.getRefreshTokenValiditySeconds())));
    }

    @Test
    @Ignore("Not implemented yet")
    public void create_client_details_with_invalid_data() throws Exception {
        // TODO
    }

    @Test
    @Ignore("Not implemented yet")
    public void create_client_details() throws Exception {
        // TODO
    }
    
    @Test
    @Ignore("Not implemented yet")
    public void reset_client_secret() throws Exception {
        
    }

    @Test
    @Ignore("Not implemented yet")
    public void update_client_details_for_invalid_data() throws Exception {
        // TODO
    }

    @Test
    @Ignore("Not implemented yet")
    public void update_client_details_for_client_id_does_not_match_url() throws Exception {
        // TODO
    }

    @Test
    @Ignore("Not implemented yet")
    public void update_client_details() throws Exception {
        // TODO
    }

    @Test
    public void remove_client_details_for_unkown_client_id() throws Exception {
        doThrow(NoSuchClientException.class).when(clientRegistrationService).removeClientDetails("666");

        mock.perform(delete(BASE_URI + 666)).andExpect(status().isNotFound());
    }

    @Test
    public void remove_client_details() throws Exception {
        mock.perform(delete(BASE_URI + 123)).andExpect(status().isNoContent());

        verify(clientRegistrationService).removeClientDetails("123");
    }

}