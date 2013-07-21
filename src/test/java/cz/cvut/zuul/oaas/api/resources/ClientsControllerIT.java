package cz.cvut.zuul.oaas.api.resources;

import cz.cvut.zuul.oaas.Factories;
import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.services.ClientsService;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@ContextConfiguration
public class ClientsControllerIT extends AbstractResourceIT {

    private static final String
            BASE_URI = "/v1/clients/",
            MIME_TYPE_JSON = "application/json;charset=UTF-8";

    // JSON attributes
    private static final String
            CLIENT_ID = "client_id",
            CLIENT_SECRET = "client_secret",
            RESOURCE_IDS = "resource_ids",
            GRANT_TYPES = "authorized_grant_types",
            REDIRECT_URI = "redirect_uri",
            ACCESS_TOKEN_VALIDITY = "access_token_validity",
            REFRESH_TOKEN_VALIDITY = "refresh_token_validity";


    @Configuration static class Context {

        @Bean ClientsService clientsService() {
            return Mockito.mock(ClientsService.class);
        }
        @Bean ClientsController controller() {
            return new ClientsController();
        }
    }

    @Autowired ClientsService clientsService;
    @Autowired MockMvc mockMvc;


    @Test
    public void get_client_details_for_non_existing_client_id() throws Exception {
        doThrow(NoSuchClientException.class).when(clientsService).findClientById("666");

        mockMvc.perform(get(BASE_URI + 666).accept(APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void get_client_details() throws Exception {
        ClientDTO expected = Factories.createRandomClientDTO("42");
        when(clientsService.findClientById("42")).thenReturn(expected);

        mockMvc.perform(get(BASE_URI + 42)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MIME_TYPE_JSON))
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
        doThrow(NoSuchClientException.class).when(clientsService).removeClient("666");

        mockMvc.perform(delete(BASE_URI + 666)).andExpect(status().isNotFound());
    }

    @Test
    public void remove_client_details() throws Exception {
        mockMvc.perform(delete(BASE_URI + 123)).andExpect(status().isNoContent());

        verify(clientsService).removeClient("123");
    }

}
