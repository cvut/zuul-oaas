package cz.cvut.zuul.oaas.generators;

import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.util.UUID;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class UUIDStringGenerator implements StringKeyGenerator {

    public String generateKey() {
        return UUID.randomUUID().toString();
    }
}
