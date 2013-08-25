package cz.cvut.zuul.oaas.services;

import cz.cvut.oauth.provider.spring.TokenInfo;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface TokensService {

    TokenInfo getTokenInfo(String tokenValue);
}
