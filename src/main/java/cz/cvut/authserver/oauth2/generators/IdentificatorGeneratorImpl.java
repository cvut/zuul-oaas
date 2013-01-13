package cz.cvut.authserver.oauth2.generators;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class IdentificatorGeneratorImpl implements IdentificatorGenerator {
    
    private RandomData randomData;
    
    public IdentificatorGeneratorImpl(){
        init();
    }
    
    private void init(){
        randomData = new RandomDataImpl(); 
    }

    @Override
    public Long generateIdentificator() {
        return new Long(randomData.nextSecureLong(10000000L, 999999999L));
    }
   
}
