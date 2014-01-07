package cz.cvut.zuul.oaas.api.services;

import cz.cvut.zuul.oaas.api.models.TokenInfo;
import cz.cvut.zuul.oaas.api.models.TokenDTO;

public interface TokensService {

    TokenDTO getToken(String tokenValue);

    TokenInfo getTokenInfo(String tokenValue);

    void invalidateToken(String tokenValue);
}
