package cz.cvut.zuul.oaas.oauth2;

import cz.cvut.zuul.oaas.repos.ClientsRepo;
import cz.cvut.zuul.oaas.models.Client;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
 */
@Slf4j
public class LockableClientUserDetailsService implements UserDetailsService {

    private @Setter ClientsRepo clientsRepo;
    private String emptyPassword = "";


    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        Client client = clientsRepo.findOne(clientId);

        String clientSecret = client.getClientSecret();
        if (isBlank(clientSecret)) {
            clientSecret = emptyPassword;
        }

        if (log.isInfoEnabled() && client.isLocked()) {
            log.info("Locked client loaded: {}", client);
        }
        return new User(clientId, clientSecret, true, true, true, !client.isLocked(), client.getAuthorities());
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
