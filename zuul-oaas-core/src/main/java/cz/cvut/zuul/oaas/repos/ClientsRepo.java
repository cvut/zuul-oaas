package cz.cvut.zuul.oaas.repos;

import cz.cvut.zuul.oaas.models.Client;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.CrudRepository;

public interface ClientsRepo extends CrudRepository<Client, String> {

    void updateClientSecret(String clientId, String secret) throws EmptyResultDataAccessException;

}
