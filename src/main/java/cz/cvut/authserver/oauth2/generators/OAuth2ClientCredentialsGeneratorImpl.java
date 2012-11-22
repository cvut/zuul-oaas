package cz.cvut.authserver.oauth2.generators;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class OAuth2ClientCredentialsGeneratorImpl implements OAuth2ClientCredentialsGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2ClientCredentialsGeneratorImpl.class);
    private SecureRandom prng;
    private MessageDigest sha;
    private static final int KEY_SIZE = 16;

    public OAuth2ClientCredentialsGeneratorImpl() {
        init();
    }

    private void init() {
        try {
            prng = SecureRandom.getInstance("SHA1PRNG");
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            LOG.error("Could not instantiate OAuth2CredentialsGeneratorImpl:" + ex.getMessage());
        }
    }

    @Override
    public String generateClientId() {
        return generateKey();
    }

    @Override
    public String generateClientSecret() {
        return generateKey();
    }

    private String generateKeyWithSpringSecurityCrypto() {
        BytesKeyGenerator generator = KeyGenerators.secureRandom(KEY_SIZE);
        byte[] bytes = generator.generateKey();
        String encodedHexString = new String(Hex.encodeHex(bytes));
        return encodedHexString;
    }

    private String generateKey() {
        String randomNum = new Integer(prng.nextInt()).toString();
        byte[] result = sha.digest(randomNum.getBytes());
        String key = new String(Hex.encodeHex(result));
        return key;
    }
}
