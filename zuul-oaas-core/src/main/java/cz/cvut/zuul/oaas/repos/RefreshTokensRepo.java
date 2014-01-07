package cz.cvut.zuul.oaas.repos;

import cz.cvut.zuul.oaas.models.PersistableRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokensRepo extends CrudRepository<PersistableRefreshToken, String> {

    void deleteByClientId(String clientId);
}
