package cz.cvut.authserver.oauth2.provider;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

/**
 * @author Tomas Mano
 *
 */
@Ignore
public class TestBootstrap {

    @Test
    public void testRootContext() throws Exception {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.getEnvironment().setActiveProfiles("inMemory");
        context.load(new FileSystemResource("src/main/webapp/WEB-INF/spring/root-context.xml"));
        context.close();
    }
}
