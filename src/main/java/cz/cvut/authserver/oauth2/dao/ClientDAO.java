package cz.cvut.authserver.oauth2.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import java.util.List;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface ClientDAO {

    List<ClientDetails> findAll();

    ClientDetails findOne(String clientId);

    void save(ClientDetails clientDetails) throws DuplicateKeyException;

    void update(ClientDetails clientDetails) throws EmptyResultDataAccessException;

    void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException;

    void delete(String clientId);

    boolean exists(String clientId);
}
