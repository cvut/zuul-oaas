package cz.cvut.zuul.oaas.api.models

import cz.cvut.zuul.oaas.api.test.ValidatorUtils
import cz.cvut.zuul.oaas.api.test.ApiObjectFactory
import groovy.json.JsonSlurper
import org.codehaus.jackson.map.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static cz.cvut.zuul.oaas.test.Assertions.assertThat
import static cz.cvut.zuul.oaas.api.test.ValidatorUtils.*
import static org.codehaus.jackson.map.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Unroll
@Mixin(ApiObjectFactory)
class ResourceDTOTest extends Specification {

    @Delegate
    static ValidatorUtils validator = createValidator(ResourceDTO)

    @Shared mapper = new ObjectMapper(propertyNamingStrategy: CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)


    void 'should marshall to JSON and vice versa'() {
        when:
            def output = mapper.writeValueAsString(input)
        then:
            with(new JsonSlurper().parseText(output)) {
                resource_id  == input.resourceId
                base_url     == input.baseUrl
                description  == input.description
                name         == input.name
                version      == input.version
                visibility   == input.visibility

                auth.scopes*.name         == input.auth.scopes*.name
                auth.scopes*.description  == input.auth.scopes*.description
                auth.scopes*.secured      == input.auth.scopes*.secured
            }
        when:
            def readed = mapper.readValue(output, ResourceDTO)
        then:
            assertThat( readed ).equalsTo( input ).inAllProperties()
        where:
            input = build(ResourceDTO)
    }


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
