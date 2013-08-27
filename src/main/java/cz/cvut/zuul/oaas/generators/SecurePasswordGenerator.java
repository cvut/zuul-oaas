package cz.cvut.zuul.oaas.generators;

import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Setter
public class SecurePasswordGenerator implements StringKeyGenerator {

    /**
     * The length of random password to generate. The default value is 32.
     */
    private int length = 32;

    /**
     * A source of randomness. {@link SecureRandom} is used by default.
     */
    private Random randomGenerator = new SecureRandom();


    public String generateKey() {
        return RandomStringUtils.random(length, 0, 0, true, true, null, randomGenerator);
    }
}
