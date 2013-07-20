package cz.cvut.zuul.oaas.generators;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface IdentifierGenerator {

    /**
     * Generates random basic identifier.
     * 
     * @return generated identifier
     */
    Long generateBasicIdentifier();
    
    /**
     * Generates random identifier with included arg value in readable form.
     *
     * @param arg value to be included in identifier value (some characters might be omitted)
     * @return generated identifier
     */
    String generateArgBasedIdentifier(String arg);

}
