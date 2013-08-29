package cz.cvut.zuul.oaas.oauth2;

import cz.cvut.zuul.oaas.repos.ClientsRepo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * This is implementation of {@link ClientDetailsService} interface that
 * basically delegates calls to {@link cz.cvut.zuul.oaas.repos.ClientsRepo}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Slf4j
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private @Setter ClientsRepo clientsRepo;


    public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {
        log.debug("Loading client: [{}]", clientId);
        ClientDetails result = clientsRepo.findOne(clientId);

        if (result == null) {
            throw OAuth2Exception.create(OAuth2Exception.INVALID_CLIENT, "No such client found with id = " + clientId);
        }
        return result;
    }
}
