package cz.cvut.zuul.oaas.repos.mongo.converters;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.stereotype.Component;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Component
public class RefreshTokenWriterConverter extends AutoRegisteredConverter<OAuth2RefreshToken, String> {

    public String convert(OAuth2RefreshToken token) {
        return token.getValue();
    }
}
