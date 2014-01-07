package cz.cvut.zuul.oaas.api.exceptions;

public class NoSuchClientException extends NotFoundException {

    public NoSuchClientException(String message, Object... args) {
        super(message, args);
    }
}
