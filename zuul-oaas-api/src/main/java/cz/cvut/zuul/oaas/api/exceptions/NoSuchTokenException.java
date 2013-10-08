package cz.cvut.zuul.oaas.api.exceptions;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class NoSuchTokenException extends NotFoundException {

    public NoSuchTokenException(String message, Object... args) {
        super(message, args);
    }
}
