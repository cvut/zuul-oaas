package cz.cvut.zuul.oaas.api.exceptions;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class NoSuchResourceException extends NotFoundException {

    public NoSuchResourceException(String message, Object... args) {
        super(message, args);
    }
}
