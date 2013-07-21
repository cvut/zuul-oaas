package cz.cvut.zuul.oaas.api.resources;

import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.dao.AccessTokenDAO;
import cz.cvut.zuul.oaas.models.PersistableAccessToken;
import cz.cvut.zuul.oaas.services.ClientsService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static cz.cvut.zuul.oaas.Factories.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@ContextConfiguration
public class TokensControllerIT extends AbstractResourceIT {

    private static final String
            TOKEN_DETAILS_URI = "/v1/tokens/",
            MIME_TYPE_JSON = "application/json;charset=UTF-8";

    // JSON attributes
    private static final String
            TOKEN_VALUE = "token_value",
            TOKEN_TYPE = "token_type",
            EXPIRATION = "expired",
            TOKEN_DENIED = "token_denied",
            CLIENT_LOCKED = "client_locked",
            SCOPES = "scopes";

    @Configuration static class Context {

        @Bean AccessTokenDAO dao() {
            return Mockito.mock(AccessTokenDAO.class);
        }
        @Bean ClientsService clientsService() {
            return Mockito.mock(ClientsService.class);
        }
        @Bean TokensController controller() {
            return new TokensController();
        }
    }

    @Autowired AccessTokenDAO accessTokenDAO;
    @Autowired ClientsService clientsService;
    @Autowired MockMvc mockMvc;


    public @Test void check_non_existing_token() throws Exception {
        doThrow(InvalidTokenException.class).when(accessTokenDAO).findOne("666");

        mockMvc.perform(get(TOKEN_DETAILS_URI + 666)
                .accept(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    public @Test void check_get_token_details() throws Exception {
        OAuth2AccessToken expToken = createRandomAccessToken();
        OAuth2Authentication expAuth = createRandomOAuth2Authentication(false);
        String clientId = expAuth.getAuthorizationRequest().getClientId();

        ClientDTO expClientDTO = createRandomClientDTO(clientId);

        doReturn(new PersistableAccessToken(expToken, expAuth)).when(accessTokenDAO).findOne(expToken.getValue());
        doReturn(expClientDTO).when(clientsService).findClientById(clientId);

        mockMvc.perform(get(TOKEN_DETAILS_URI + expToken.getValue())
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MIME_TYPE_JSON))
                .andExpect(jsonPath(TOKEN_VALUE, equalTo(expToken.getValue())))
                .andExpect(jsonPath(TOKEN_TYPE, equalTo(expToken.getTokenType())))
                .andExpect(jsonPath(TOKEN_DENIED, equalTo(expAuth.getAuthorizationRequest().isDenied())))
                .andExpect(jsonPath(CLIENT_LOCKED, equalTo(expClientDTO.isLocked())))
                .andExpect(jsonPath(EXPIRATION, equalTo(expToken.getExpiration().getTime())))
                .andExpect(jsonPath(SCOPES, hasItems(expToken.getScope().toArray())));
    }


    public @Test void invalidate_non_existing_token() throws Exception {
        when(accessTokenDAO.exists("666")).thenReturn(false);

        mockMvc.perform(delete(TOKEN_DETAILS_URI + 666))
                .andExpect(status().isConflict());
    }

    public @Test void invalidate_token() throws Exception {
        OAuth2AccessToken expToken = createRandomAccessToken();

        when(accessTokenDAO.exists(expToken.getValue())).thenReturn(true);

        mockMvc.perform(delete(TOKEN_DETAILS_URI + expToken))
                .andExpect(status().isNoContent());
    }
}
