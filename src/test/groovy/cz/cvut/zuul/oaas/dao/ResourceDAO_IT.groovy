package cz.cvut.zuul.oaas.dao

import cz.cvut.zuul.oaas.models.Resource
import org.springframework.beans.factory.annotation.Autowired

import static cz.cvut.zuul.oaas.models.enums.Visibility.HIDDEN
import static cz.cvut.zuul.oaas.models.enums.Visibility.PUBLIC
import static cz.cvut.zuul.oaas.test.Assertions.assertThat

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ResourceDAO_IT extends AbstractDAO_IT<Resource>{

    @Autowired ResourceDAO dao

    void assertIt(Resource actual, Resource expected) {
        assertThat (actual) equalsTo (expected) inAllPropertiesExcept ('auth')
        assert actual.auth.scopes == expected.auth.scopes
    }


    def 'find all public resources'() {
        setup:
            ([PUBLIC.toString()] * 3 + [HIDDEN.toString()] * 2).each { visibility ->
                def entity = build(Resource, [visibility: visibility])
                dao.save(entity)
            }
        expect:
            dao.findAllPublic().size() == 3
    }
}
