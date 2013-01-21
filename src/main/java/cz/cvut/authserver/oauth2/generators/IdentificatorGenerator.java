package cz.cvut.authserver.oauth2.generators;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface IdentificatorGenerator {

    /**
     * Generates random basic identificator.
     * 
     * @return generated identificator
     */
    public Long generateBasicIdentificator();
    
    /**
     * Generates random identificator with included arg value in readable form.
     *
     * @param arg value to be included in identificator value (some characters might be omitted)
     * @return generated identificator
     */
    public String generateArgBasedIdentificator(String arg);

}
