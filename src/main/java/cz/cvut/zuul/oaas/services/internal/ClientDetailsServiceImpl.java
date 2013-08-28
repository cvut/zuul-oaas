package cz.cvut.zuul.oaas.services.internal;

import cz.cvut.zuul.oaas.dao.ClientDAO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * This is implementation of {@link ClientDetailsService} interface that
 * basically delegates calls to {@link ClientDAO}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Slf4j
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private @Setter ClientDAO clientDAO;


    public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {
        ClientDetailsServiceImpl.log.debug("Loading client: [{}]", clientId);
        ClientDetails result = clientDAO.findOne(clientId);

        if (result == null) {
            throw OAuth2Exception.create(OAuth2Exception.INVALID_CLIENT, "No such client found with id = " + clientId);
        }
        return result;
    }
}
