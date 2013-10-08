package cz.cvut.zuul.oaas.api.exceptions;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class NotFoundException extends ZuulException {

    public NotFoundException(String message, Object... args) {
        super(message, args);
    }
}
