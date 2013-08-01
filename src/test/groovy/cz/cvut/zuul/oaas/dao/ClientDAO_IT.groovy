package cz.cvut.zuul.oaas.dao

import cz.cvut.zuul.oaas.models.Client
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ClientDAO_IT extends AbstractDAO_IT<Client> {

    @Autowired ClientDAO dao


    def 'update secret for non existing client'() {
        when:
            dao.updateClientSecret('unknown-id', 'new-secret')
        then:
            thrown(EmptyResultDataAccessException)

    }

    def 'update client secret'() {
        setup:
            def client = build(Client)
            assert client.clientSecret && client.clientSecret != 'new-secret'
            dao.save(client)
        when:
            dao.updateClientSecret(client.clientId, 'new-secret')
        then:
            dao.findOne(client.clientId).clientSecret == 'new-secret'
    }

    def 'update redirect URIs'() {
        setup:
            def client = build(Client, [registeredRedirectUri: ['http://example.org']])
            dao.save(client)
        when:
            def expected = dao.findOne(client.clientId)
            expected.registeredRedirectUri << 'https://cool.org'
            dao.save(expected)
        then:
            def actual = dao.findOne(client.clientId)
            actual.registeredRedirectUri == expected.registeredRedirectUri
    }
}
