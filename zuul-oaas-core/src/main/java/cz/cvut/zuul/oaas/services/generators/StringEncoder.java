package cz.cvut.zuul.oaas.services.generators;

public interface StringEncoder {

    /**
     * @param value A String to encode.
     * @return The encoded String.
     */
    String encode(String value);
}
