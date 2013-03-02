package cz.cvut.authserver.oauth2.dao;

import cz.cvut.authserver.oauth2.models.Client;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.List;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface ClientDAO {

    List<Client> findAll();

    Client findOne(String clientId);

    void save(Client client) throws DuplicateKeyException;

    void update(Client client) throws EmptyResultDataAccessException;

    void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException;

    void delete(String clientId);

    boolean exists(String clientId);
}
