package cz.cvut.zuul.oaas.services.generators;

import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.util.UUID;

public class UUIDStringGenerator implements StringKeyGenerator {

    public String generateKey() {
        return UUID.randomUUID().toString();
    }
}
