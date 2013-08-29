package cz.cvut.zuul.oaas.dao;

import cz.cvut.zuul.oaas.models.PersistableRefreshToken;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface RefreshTokensRepo extends CrudRepository<PersistableRefreshToken, String> {

    void deleteByClientId(String clientId);
}
