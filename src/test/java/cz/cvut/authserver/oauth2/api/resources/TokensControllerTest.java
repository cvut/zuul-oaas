package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import cz.cvut.authserver.oauth2.dao.AccessTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableAccessToken;
import cz.cvut.authserver.oauth2.services.ClientsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.web.server.MockMvc;

import static cz.cvut.authserver.oauth2.Factories.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import org.junit.Ignore;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class TokensControllerTest {

    private static final String
            GET_TOKEN_DETAILS_URI = "/v1/tokens/",
            MIME_TYPE_JSON = "application/json;charset=UTF-8";

    // JSON attributes
    private static final String
            TOKEN_VALUE = "token_value",
            TOKEN_TYPE = "token_type",
            EXPIRATION = "expired",
            TOKEN_DENIED = "token_denied",
            CLIENT_LOCKED = "client_locked",
            SCOPES = "scopes";

    private @Mock AccessTokenDAO accessTokenDAO;
    private @Mock ClientsService clientsService;
    private @InjectMocks TokensController tokensController;

    private MockMvc controller;



    public @Before void buildMocks() {
        controller = standaloneSetup(tokensController).build();
    }


    public @Test void check_non_existing_token() throws Exception {
        doThrow(InvalidTokenException.class).when(accessTokenDAO).findOne("666");

        controller.perform(get(GET_TOKEN_DETAILS_URI + 666)
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

        controller.perform(get(GET_TOKEN_DETAILS_URI + expToken.getValue())
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(MIME_TYPE_JSON))
                .andExpect(jsonPath(TOKEN_VALUE, equalTo(expToken.getValue())))
                .andExpect(jsonPath(TOKEN_TYPE, equalTo(expToken.getTokenType())))
                .andExpect(jsonPath(TOKEN_DENIED, equalTo(expAuth.getAuthorizationRequest().isDenied())))
                .andExpect(jsonPath(CLIENT_LOCKED, equalTo(expClientDTO.isLocked())))
                .andExpect(jsonPath(EXPIRATION, equalTo(expToken.getExpiration().getTime())))
                .andExpect(jsonPath(SCOPES, hasItems(expToken.getScope().toArray())));
    }

    public @Test @Ignore("Not implemented yet") void test_invalidate_token() throws Exception {
        //TODO
    }

}
