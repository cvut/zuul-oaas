package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.models.Visibility
import org.springframework.beans.factory.annotation.Autowired

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

class ResourcesRepoIT extends AbstractRepoIT<Resource>{

    @Autowired ResourcesRepo repo

    void assertIt(Resource actual, Resource expected) {
        assertThat (actual) equalsTo (expected) inAllProperties()
    }


    def 'find all public resources'() {
        setup:
            ([Visibility.PUBLIC] * 3 + [Visibility.HIDDEN] * 2).each { visibility ->
                def entity = build(Resource, [visibility: visibility])
                repo.save(entity)
            }
        expect:
            repo.findAllPublic().size() == 3
    }
}
