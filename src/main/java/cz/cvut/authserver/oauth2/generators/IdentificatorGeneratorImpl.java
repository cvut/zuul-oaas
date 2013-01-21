package cz.cvut.authserver.oauth2.generators;

import org.apache.commons.lang.StringUtils;
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
    public Long generateBasicIdentificator() {
        return new Long(randomData.nextSecureLong(10000000L, 999999999L));
    }

    @Override
    public String generateArgBasedIdentificator(String arg) {
        // Replace whitespaces with '-'
        String whitespacesReplaced = arg.replaceAll("\\s+", "-");  
        
        // Remove anything that is not english character or number and convert to lower case
        String replaced = whitespacesReplaced.replaceAll("[.!?<>/:\"\'&*()_+={}@~|]", "").toLowerCase();
        
        // Concat with random 4-digit number
        String generated = replaced.concat("-"+ new Integer(randomData.nextInt(100000, 999999)));
        
        return generated;
    }
    
}
