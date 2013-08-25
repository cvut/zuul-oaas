package cz.cvut.zuul.oaas.services;

import cz.cvut.oauth.provider.spring.TokenInfo;
import cz.cvut.zuul.oaas.dao.AccessTokenDAO;
import cz.cvut.zuul.oaas.dao.ClientDAO;
import cz.cvut.zuul.oaas.models.Client;
import cz.cvut.zuul.oaas.models.ExtendedUserDetails;
import cz.cvut.zuul.oaas.models.PersistableAccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Service;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Service
public class TokensServiceImpl implements TokensService {

    private AccessTokenDAO accessTokenDAO;
    private ClientDAO clientDAO;


    public TokenInfo getTokenInfo(String tokenValue) {
        PersistableAccessToken accessToken = accessTokenDAO.findOne(tokenValue);

        // first check if token is recognized and if it is not expired
        if (accessToken == null) {
            throw new InvalidTokenException("Token was not recognised");
        }
        if (accessToken.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }

        Client client = clientDAO.findOne(accessToken.getAuthenticatedClientId());
        if (client == null) {
            throw new InvalidTokenException("Client doesn't exist anymore");
        }
        if (client.isLocked()) {
            throw new InvalidTokenException("Client is locked");
        }

        AuthorizationRequest clientAuth = accessToken.getAuthentication().getAuthorizationRequest();
        Authentication userAuth = accessToken.getAuthentication().getUserAuthentication();

        TokenInfo o = new TokenInfo();
        o.setExpiresIn( accessToken.getExpiresIn() );
        o.setScope( accessToken.getScope() );
        o.setAudience( clientAuth.getResourceIds() );
        o.setClientId( clientAuth.getClientId() );
        o.setClientAuthorities( clientAuth.getAuthorities() );

        if (userAuth != null) {
            o.setUserAuthorities( userAuth.getAuthorities() );
            o.setUserId( userAuth.getName() );

            if (userAuth.getPrincipal() instanceof ExtendedUserDetails) {
                ExtendedUserDetails user = (ExtendedUserDetails) userAuth.getPrincipal();
                o.setUserEmail( user.getEmail() );
            }
        }
        return o;
    }

    public void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.accessTokenDAO = accessTokenDAO;
    }

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }
}
