package cz.cvut.zuul.oaas.services;

import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.dao.AccessTokenDAO;
import cz.cvut.zuul.oaas.dao.ClientDAO;
import cz.cvut.zuul.oaas.dao.RefreshTokenDAO;
import cz.cvut.zuul.oaas.models.Client;
import cz.cvut.zuul.oaas.support.GrantedAuthorityConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PACKAGE;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Service
@Setter @Slf4j
public class ClientsServiceImpl implements ClientsService {

    private static final List<GrantedAuthority> DEFAULT_AUTHORITIES =  AuthorityUtils.createAuthorityList("ROLE_CLIENT");

    private ClientDAO clientDAO;
    private AccessTokenDAO accessTokenDAO;
    private RefreshTokenDAO refreshTokenDAO;

    private StringKeyGenerator clientIdGenerator;
    private StringKeyGenerator secretGenerator;
    private PasswordEncoder secretEncoder;

    /**
     * Orika Mapper Factory to be configured and used for mapping between entity
     * and DTO objects. If no factory is provided, then new one will be created.
     *
     * @see {@link #setupMapper()}
     */
    private MapperFactory mapperFactory;

    @Setter(NONE) @Getter(PACKAGE)
    private MapperFacade mapper;



    public ClientDTO findClientById(String clientId) throws NoSuchClientException {
        Client client = clientDAO.findOne(clientId);

        if (client == null) {
            throw new NoSuchClientException(String.format("Client with id [%s] doesn't exists.", clientId));
        }
        return mapper.map(client, ClientDTO.class);
    }

    public String createClient(ClientDTO clientDTO) throws ClientAlreadyExistsException {
        Client client = mapper.map(clientDTO, Client.class);

        String clientId;
        do {
            log.debug("Generating unique clientId...");
            clientId = clientIdGenerator.generateKey();
        } while (clientDAO.exists(clientId));

        String plainSecret = secretGenerator.generateKey();
        String encodedSecret = secretEncoder.encode(plainSecret);

        client.setClientId(clientId);
        client.setClientSecret(encodedSecret);

        if (isEmpty(client.getAuthorities())) {
            client.setAuthorities(DEFAULT_AUTHORITIES);
        }

        log.info("Saving a new client: [{}]", client);
        clientDAO.save(client);

        return clientId;
    }

    public void updateClient(ClientDTO clientDTO) throws NoSuchClientException {
        log.info("Updating client: [{}]", clientDTO);

        assertClientExists(clientDTO.getClientId());
        clientDAO.save(mapper.map(clientDTO, Client.class));
    }

    public void removeClient(String clientId) throws NoSuchClientException {
        log.info("Removing client: [{}]", clientId);
        assertClientExists(clientId);

        //remove associated tokens
        accessTokenDAO.deleteByClientId(clientId);
        refreshTokenDAO.deleteByClientId(clientId);

        clientDAO.delete(clientId);
    }

    public void resetClientSecret(String clientId) throws NoSuchClientException {
        log.info("Resetting secret for client: [{}]", clientId);

        String plain = secretGenerator.generateKey();
        String encoded = secretEncoder.encode(plain);

        try {
            clientDAO.updateClientSecret(clientId, encoded);

        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchClientException(ex.getMessage(), ex);
        }
    }


    private void assertClientExists(String clientId) {
        if (! clientDAO.exists(clientId)) {
            throw new NoSuchClientException("No such client with id = " + clientId);
        }
    }

    @PostConstruct void setupMapper() {
        MapperFactory factory = defaultIfNull(mapperFactory, new Builder().build());

        factory.getConverterFactory()
                .registerConverter(new GrantedAuthorityConverter());

        factory.registerClassMap(factory
                .classMap(Client.class, ClientDTO.class)
                .byDefault()
        );
        mapper = factory.getMapperFacade();
    }
}
