package cz.cvut.authserver.oauth2.generators;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Service
public class OAuth2ClientCredentialsGeneratorImpl implements OAuth2ClientCredentialsGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2ClientCredentialsGeneratorImpl.class);
    private SecureRandom prng;
    private static final int KEY_SIZE = 64;
    
    private static char[] alphaNumeric = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public OAuth2ClientCredentialsGeneratorImpl() {
        init();
    }

    private void init() {
        try {
            prng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            LOG.error("Could not instantiate OAuth2CredentialsGeneratorImpl:" + ex.getMessage());
        }
    }

    @Override
    public String generateClientId() {
        return generateRandomKey();
    }

    @Override
    public String generateClientSecret() {
        return generateRandomKey();
    }

    private String generateRandomKey() {
        StringBuilder builder = new StringBuilder();
        char c;
        for (int i = 0; i < KEY_SIZE; i++) {
            c = alphaNumeric[prng.nextInt(alphaNumeric.length)];
            builder.append(c);
        }
        return builder.toString();
    }
}
