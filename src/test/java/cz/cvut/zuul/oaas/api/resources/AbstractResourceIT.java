package cz.cvut.zuul.oaas.api.resources;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Abstract base class for TestNG integration tests of REST resources
 * implemented as Spring controllers. It initializes a minimal common
 * Spring context with MockMvc.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@WebAppConfiguration
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractResourceIT {

    @Configuration @EnableWebMvc
    protected static class CommonContextConfiguration {

        @Autowired WebApplicationContext context;

        @Bean CommonExceptionHandler exceptionHandler() {
            return new CommonExceptionHandler();
        }
        @Bean MockMvc mockMvc() {
            return webAppContextSetup(context).build();
        }
    }
}
