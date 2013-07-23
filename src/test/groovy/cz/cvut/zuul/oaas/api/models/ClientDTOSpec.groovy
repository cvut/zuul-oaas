package cz.cvut.zuul.oaas.api.models

import cz.cvut.zuul.oaas.models.enums.AuthorizationGrant
import cz.cvut.zuul.oaas.test.ValidatorUtils
import spock.lang.Specification
import spock.lang.Unroll

import static cz.cvut.zuul.oaas.test.ValidatorUtils.*

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Unroll
class ClientDTOSpec extends Specification {

    @Delegate
    static ValidatorUtils validator = createValidator(ClientDTO.class)


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
