package cz.cvut.zuul.oaas.repos.mongo.converters;

import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenReaderConverter extends AutoRegisteredConverter<String, OAuth2RefreshToken> {

    public OAuth2RefreshToken convert(String value) {
        return new DefaultOAuth2RefreshToken(value);
    }
}
