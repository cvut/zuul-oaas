package cz.cvut.authserver.oauth2.generators;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class IdentifierGeneratorImpl implements IdentifierGenerator {
    
    private RandomData randomData;


    public IdentifierGeneratorImpl(){
        randomData = new RandomDataImpl();
    }

    @Override
    public Long generateBasicIdentifier() {
        return randomData.nextSecureLong(10000000L, 999999999L);
    }

    @Override
    public String generateArgBasedIdentifier(String arg) {
        // Replace whitespaces with '-'
        String whitespacesReplaced = arg.replaceAll("\\s+", "-");  
        
        // Remove anything that is not english character or number and convert to lower case
        String replaced = whitespacesReplaced.replaceAll("[.!?<>/:\"\'&*()_+={}@~|]", "").toLowerCase();
        
        // Concat with random 4-digit number
        return replaced.concat("-"+ randomData.nextInt(100000, 999999));
    }
    
}
