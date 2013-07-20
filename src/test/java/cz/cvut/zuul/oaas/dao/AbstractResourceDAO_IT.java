package cz.cvut.zuul.oaas.dao;

import cz.cvut.zuul.oaas.models.Resource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static cz.cvut.zuul.oaas.Factories.createResource;
import static cz.cvut.zuul.oaas.TestUtils.assertEachEquals;
import static org.junit.Assert.*;

/**
 * Base class for all {@link ResourceDAO} implementations.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dao-test.xml")
public abstract class AbstractResourceDAO_IT {

    protected @Autowired ResourceDAO dao;


    public @Test void find_resource_by_non_existing_id() {

        assertNull(dao.findOne("nonExistingResourceId"));
    }

    public @Test void save_and_find_resource() {

        Resource expected = createResource("foo");

        dao.save(expected);
        Resource actual = dao.findOne("foo");

        assertNotNull(actual);
        assertEquals(expected.getBaseUrl(), actual.getBaseUrl());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getVersion(), actual.getVersion());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getVisibility(), actual.getVisibility());

        assertNotNull(actual.getAuth());
        assertEachEquals(expected.getAuth().getScopes(), actual.getAuth().getScopes());
    }


    public @Test void delete_non_existing_resource() {

        dao.delete("nonExistingResourceId");
    }

    public @Test void delete() {

        dao.save(createResource("foo"));

        assertNotNull(dao.findOne("foo"));

        dao.delete("foo");

        assertNull(dao.findOne("foo"));
    }


    @Ignore //TODO
    public @Test void findAll() {

    }

    @Ignore // TODO
    public @Test void findAllPublic() {

    }

}
