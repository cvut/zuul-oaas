package cz.cvut.zuul.oaas.generators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration ("classpath:/api-context-test.xml")
public class OAuth2ClientCredentialsGeneratorTest {
    
    @Autowired
    private OAuth2ClientCredentialsGenerator generator;
    
    @Test
    public void test(){

    }
}
