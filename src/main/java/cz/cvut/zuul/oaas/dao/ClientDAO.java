package cz.cvut.zuul.oaas.dao;

import cz.cvut.zuul.oaas.models.Client;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface ClientDAO extends CrudRepository<Client, String> {

    void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException;

}
