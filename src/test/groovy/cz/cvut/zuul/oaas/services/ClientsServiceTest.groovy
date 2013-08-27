package cz.cvut.zuul.oaas.services

import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.dao.AccessTokenDAO
import cz.cvut.zuul.oaas.dao.ClientDAO
import cz.cvut.zuul.oaas.dao.RefreshTokenDAO
import cz.cvut.zuul.oaas.models.Client
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import ma.glasnost.orika.MapperFacade
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.keygen.StringKeyGenerator
import org.springframework.security.oauth2.provider.NoSuchClientException
import spock.lang.Specification

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Mixin(ObjectFactory)
class ClientsServiceTest extends Specification {

    def clientDao = Mock(ClientDAO)
    def accessTokenDao = Mock(AccessTokenDAO)
    def refreshTokenDao = Mock(RefreshTokenDAO)
    def clientIdGenerator = Mock(StringKeyGenerator)
    def secretGenerator = Mock(StringKeyGenerator)
    def mapper = Mock(MapperFacade)

    def service = new ClientsServiceImpl(
            clientDAO: clientDao,
            accessTokenDAO: accessTokenDao,
            refreshTokenDAO: refreshTokenDao,
            clientIdGenerator: clientIdGenerator,
            secretGenerator: secretGenerator,
            mapper: mapper
    )


    def 'find client by non existing id'() {
        when:
            service.findClientById('non-existing')
        then:
            clientDao.findOne('non-existing') >> null
            thrown(NoSuchClientException)
    }


    def 'create client'() {
        setup:
            def client = build(Client).with {
                clientId = 'irrelevant'; clientSecret = 'whatever'; return it
            }
            def generatedId = 'client-123'
            def generatedSecret = 'top-secret'

            mapper.map(_, Client) >> client
        when:
            def returnedId = service.createClient( build(ClientDTO) )
        then:
            1 * clientIdGenerator.generateKey() >> generatedId
            1 * secretGenerator.generateKey() >> generatedSecret
            1 * clientDao.save({ Client it ->
                it.clientId == generatedId
                it.clientSecret == generatedSecret
            })
            returnedId == generatedId
    }

    def 'create client with default authorities'() {
        setup:
            def client = build(Client).with {
                it.authorities = []; return it
            }
            mapper.map(_, Client) >> client
        when:
            service.createClient( build(ClientDTO) )
        then:
            1 * clientDao.save( { ! it.authorities?.isEmpty() } )
    }

    def 'create client and handle generation of already taken id'() {
        setup:
            def client = build(Client)
            def generatedId = 'client-123'

            mapper.map(_, Client) >> client
        when:
            service.createClient( build(ClientDTO) ) == generatedId
        then: 'generate unique id at the third attempt'
            3 * clientIdGenerator.generateKey() >>> ['taken-id', 'still-bad', generatedId]
            3 * clientDao.exists(_) >>> [true, true, false]
        then:
            1 * clientDao.save({ Client it ->
                it.clientId == generatedId
            })
    }


    def 'update non existing client'() {
        when:
            service.updateClient( build(ClientDTO) )
        then:
            clientDao.exists(_) >> false
            thrown(NoSuchClientException)
    }


    def 'remove existing client'() {
        setup:
            def clientId = 'client-123'
            clientDao.exists(clientId) >> true
        when:
            service.removeClient(clientId)
        then:
            1 * clientDao.delete(clientId)
            1 * accessTokenDao.deleteByClientId(clientId)
            1 * refreshTokenDao.deleteByClientId(clientId)
    }

    def 'remove non existing client'() {
        when:
            service.removeClient('non-existing')
        then:
            clientDao.exists('non-existing') >> false
            thrown(NoSuchClientException)
    }


    def 'reset client secret for existing client'() {
        when:
            service.resetClientSecret('client-123')
        then:
            1 * secretGenerator.generateKey() >> 'top-secret'
            1 * clientDao.updateClientSecret('client-123', 'top-secret')
    }

    def 'reset client secret for non existing client'() {
        when:
           service.resetClientSecret('non-existing')
        then:
            1 * clientDao.updateClientSecret('non-existing', _) >> {
                throw new EmptyResultDataAccessException(1)
            }
            thrown(NoSuchClientException)
    }
}
