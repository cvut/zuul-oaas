package cz.cvut.zuul.oaas.api.models

import cz.cvut.zuul.oaas.models.AuthorizationGrant
import cz.cvut.zuul.oaas.test.ValidatorUtils
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import groovy.json.JsonSlurper
import org.codehaus.jackson.map.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static cz.cvut.zuul.oaas.test.Assertions.assertThat
import static cz.cvut.zuul.oaas.test.ValidatorUtils.*
import static org.codehaus.jackson.map.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Unroll
@Mixin(ObjectFactory)
class ClientDTOTest extends Specification {

    @Delegate
    static ValidatorUtils validator = createValidator(ClientDTO.class)

    @Shared mapper = new ObjectMapper(propertyNamingStrategy: CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)


    void 'should marshall to JSON and vice versa'() {
        when:
            def output = mapper.writeValueAsString(input)
        then:
            with(new JsonSlurper().parseText(output)) {
                client_id              == input.clientId
                client_secret          == input.clientSecret
                scope                  == input.scope
                resource_ids           == input.resourceIds
                authorized_grant_types == input.authorizedGrantTypes
                redirect_uri           == input.registeredRedirectUri
                authorities            == input.authorities
                access_token_validity  == input.accessTokenValiditySeconds
                refresh_token_validity == input.refreshTokenValiditySeconds
                product_name           == input.productName
                client_locked          == input.locked
                client_type            == input.clientType
            }
        when:
            def readed = mapper.readValue(output, ClientDTO)
        then:
            assertThat( readed ).equalsTo( input ).inAllProperties()
        where:
            input = build(ClientDTO)
    }


    void 'scope should be #expected given #description scopes'() {
        expect:
            validate 'scope', value, expected
        where:
            description          | value                                             || expected
            'empty'              | []                                                || valid
            'valid plain'        | ['poign', 'chu_ky.B4c-0n']                        || valid
            'valid URI'          | ['urn:sample.read', 'http://cvut.cz/scope#read']  || valid
            'with illegal chars' | ['first-valid', 'spa ce', 'quote\'', 'back\\']    || invalid
            'too short'          | ['o', 'four', 'valid']                            || invalid
            'too long'           | ['c' * 257]                                       || invalid
    }

    void 'authorizedGrantTypes should be #expected given #description grants'() {
        expect:
            validate 'authorizedGrantTypes', value, expected
        where:
            description     | value                                     || expected
            'valid'         | AuthorizationGrant.values()*.toString()   || valid
            'empty'         | []                                        || invalid
            'invalid'       | ['evil-grant']                            || invalid
    }

    void 'registeredRedirectUri should be #expected given #description URI'() {
        expect:
            validate 'registeredRedirectUri', value, expected
        where:
            description     | value                                     || expected
            'empty'         | []                                        || valid
            'valid'         | ['http://cvut.cz', 'urn:ctu:oauth']       || valid
            'invalid'       | ['foo', 'baaaar']                         || invalid
            'relative'      | ['/relative/url', '../another/relative']  || invalid
            'with fragment' | ['http://cvut.cz/cool#fragment']          || invalid
            'too long'      | ['http://cvut.cz', 'c' * 242]             || invalid
    }

    void 'should be valid given empty redirect URIs when grant type is not "authorization_code"'() {
        given:
            def client = new ClientDTO(
                    registeredRedirectUri: [],
                    authorizedGrantTypes: ['client_credentials']
            )
        expect:
            isValid client
    }

    void 'should be invalid given empty redirect URIs when grant type is "authorization_code"'() {
        given:
            def client = new ClientDTO(
                    registeredRedirectUri: [],
                    authorizedGrantTypes: ['authorization_code']
            )
        expect:
            isInvalid client
    }
}
