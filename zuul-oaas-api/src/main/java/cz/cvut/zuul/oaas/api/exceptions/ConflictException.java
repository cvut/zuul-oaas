package cz.cvut.zuul.oaas.api.exceptions;

public class ConflictException extends ZuulException {

    public ConflictException(String message, Object... args) {
        super(message, args);
    }
}
