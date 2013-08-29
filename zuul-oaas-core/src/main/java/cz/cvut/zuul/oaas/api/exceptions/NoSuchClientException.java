package cz.cvut.zuul.oaas.api.exceptions;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class NoSuchClientException extends NotFoundException {

    public NoSuchClientException(String message, Object... args) {
        super(message, args);
    }
}
