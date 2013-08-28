package cz.cvut.zuul.oaas.generators;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public interface StringEncoder {

    /**
     * @param value A String to encode.
     * @return The encoded String.
     */
    String encode(String value);
}
