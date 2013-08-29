package cz.cvut.zuul.oaas.api.exceptions;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class NoSuchResourceException extends RuntimeException{

    public NoSuchResourceException() {
    }

    public NoSuchResourceException(String message) {
        super(message);
    }

    public NoSuchResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchResourceException(Throwable cause) {
        super(cause);
    }

    public NoSuchResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
