package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.models.Client
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException

class ClientsRepoIT extends AbstractRepoIT<Client> {

    @Autowired ClientsRepo repo


    def 'update secret for non existing client'() {
        when:
            repo.updateClientSecret('unknown-id', 'new-secret')
        then:
            thrown(EmptyResultDataAccessException)

    }

    def 'update client secret'() {
        setup:
            def client = build(Client)
            assert client.clientSecret != 'new-secret'
            repo.save(client)
        when:
            repo.updateClientSecret(client.clientId, 'new-secret')
        then:
            repo.findOne(client.clientId).clientSecret == 'new-secret'
    }

    def 'update redirect URIs'() {
        setup:
            def client = build(Client, [registeredRedirectUri: ['http://example.org']])
            repo.save(client)
        when:
            def expected = repo.findOne(client.clientId)
            expected.registeredRedirectUri << 'https://cool.org'
            repo.save(expected)
        then:
            def actual = repo.findOne(client.clientId)
            actual.registeredRedirectUri == expected.registeredRedirectUri
    }
}
