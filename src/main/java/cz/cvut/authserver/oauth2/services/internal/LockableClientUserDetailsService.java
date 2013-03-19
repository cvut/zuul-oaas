package cz.cvut.authserver.oauth2.services.internal;

import cz.cvut.authserver.oauth2.dao.ClientDAO;
import cz.cvut.authserver.oauth2.models.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Service for populating OAuth2 Client as "UserDetails" that implements
 * {@link UserDetailsService} interface.
 *
 * This replaces the default implementation {@link org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService}
 * for OAuth2 and adds support for lockable Clients.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class LockableClientUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(LockableClientUserDetailsService.class);

    private ClientDAO clientDAO;
    private String emptyPassword = "";


    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        Client client = clientDAO.findOne(clientId);

        String clientSecret = client.getClientSecret();
        if (isBlank(clientSecret)) {
            clientSecret = emptyPassword;
        }

        if (LOG.isInfoEnabled() && client.isLocked()) {
            LOG.info("Locked client loaded: {}", client);
        }
        return new User(clientId, clientSecret, true, true, true, !client.isLocked(), client.getAuthorities());
    }


    @Required
    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    /**
     * This is used only to encode empty password which is used when client does
     * not have any password.
     *
     * @param passwordEncoder
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.emptyPassword = passwordEncoder.encode("");
    }
}
