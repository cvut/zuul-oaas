package cz.cvut.authserver.oauth2.converter;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @author Dave Syer
 * @author Tomas Mano
 * 
*/
public class DefaultTokenConverter implements AccessTokenConverter {

    private UserTokenConverter userTokenConverter = new DefaultUserTokenConverter();

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        AuthorizationRequest clientToken = authentication.getAuthorizationRequest();

        if (!authentication.isClientOnly()) {
            response.putAll(userTokenConverter.convertUserAuthentication(authentication.getUserAuthentication()));
        }

        response.put("client_id", clientToken.getClientId());
        response.put(OAuth2AccessToken.SCOPE, token.getScope());
        response.put(OAuth2AccessToken.TOKEN_TYPE, token.getTokenType());
        
//        if (token.getAdditionalInformation().containsKey(JwtTokenEnhancer.TOKEN_ID)) {
//            response.put(JwtTokenEnhancer.TOKEN_ID, token.getAdditionalInformation().get(JwtTokenEnhancer.TOKEN_ID));
//        }

        if (token.getExpiration() != null) {
            response.put("exp", token.getExpiration().getTime() / 1000);
        }

        response.putAll(token.getAdditionalInformation());

        if (clientToken.getResourceIds() != null && !clientToken.getResourceIds().isEmpty()) {
            response.put("resources", clientToken.getResourceIds());
        }
        return response;
    }
}
