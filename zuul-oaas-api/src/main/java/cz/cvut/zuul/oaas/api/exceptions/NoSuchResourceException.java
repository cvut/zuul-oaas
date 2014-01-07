package cz.cvut.zuul.oaas.api.exceptions;

public class NoSuchResourceException extends NotFoundException {

    public NoSuchResourceException(String message, Object... args) {
        super(message, args);
    }
}
