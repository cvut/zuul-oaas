package cz.cvut.zuul.oaas.api.exceptions;

public class NotFoundException extends ZuulException {

    public NotFoundException(String message, Object... args) {
        super(message, args);
    }
}
