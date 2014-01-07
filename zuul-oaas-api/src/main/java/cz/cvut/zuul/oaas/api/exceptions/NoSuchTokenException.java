package cz.cvut.zuul.oaas.api.exceptions;

public class NoSuchTokenException extends NotFoundException {

    public NoSuchTokenException(String message, Object... args) {
        super(message, args);
    }
}
