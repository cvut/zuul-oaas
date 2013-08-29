package cz.cvut.zuul.oaas.api.models

import cz.cvut.zuul.oaas.test.ValidatorUtils
import spock.lang.Specification
import spock.lang.Unroll

import static cz.cvut.zuul.oaas.test.ValidatorUtils.*

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Unroll
class ResourceDTOTest extends Specification {

    @Delegate
    static ValidatorUtils validator = createValidator(ResourceDTO)


    def 'baseUrl should be #expected given #description URL'() {
        expect:
            validate 'baseUrl', value, expected
        where:
            description          | value                                       || expected
            'http://'            | 'http://cvut.cz'                            || valid
            'https://'           | 'https://cvut.cz'                           || valid
            'empty'              | ''                                          || invalid
            'illegal schemas'    | 'ftp://invalid.org'                         || invalid
            'too long'           | "http://lo${ 'o' * 242 }ng.org".toString()  || invalid
    }

    def 'description should be #expected given #description string'() {
        expect:
            validate 'description', value, expected
        where:
            description     | value             || expected
            'null'          | null              || valid
            'valid'         | 'Chunky beacon'   || valid
            'too long'      | 'o' * 257         || invalid
    }

    def 'name should be #expected given #description string'() {
        expect:
            validate 'name', value, expected
        where:
            description     | value             || expected
            'valid'         | 'Chunky beacon'   || valid
            'empty'         | ''                || invalid
            'too long'      | 'o' * 257         || invalid
    }

    def 'version should be #expected given #description string'() {
        expect:
            validate 'version', value, expected
        where:
            description     | value             || expected
            'null'          | null              || valid
            'version'       | '1.0'             || valid
            'too long'      | 'o' * 257         || invalid
    }

    def 'visibility should be #expected given #description visibility'() {
        expect:
            validate 'visibility', value, expected
        where:
            description     | value             || expected
            '"hidden"'      | 'hidden'          || valid
            '"public"'      | 'public'          || valid
            'empty'         | ''                || invalid
            'invalid'       | 'invalid'         || invalid
    }
}
