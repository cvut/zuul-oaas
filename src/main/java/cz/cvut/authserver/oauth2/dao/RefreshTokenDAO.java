package cz.cvut.authserver.oauth2.dao;

import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface RefreshTokenDAO {

    PersistableRefreshToken findOne(String tokenCode);

    void save(PersistableRefreshToken persistableToken);

    void delete(OAuth2RefreshToken token);
}
