package cz.cvut.zuul.oaas.api.services;

import cz.cvut.oauth.provider.spring.TokenInfo;
import cz.cvut.zuul.oaas.api.models.TokenDTO;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface TokensService {

    TokenDTO getToken(String tokenValue);

    TokenInfo getTokenInfo(String tokenValue);

    void invalidateToken(String tokenValue);
}
