package cz.cvut.zuul.oaas.api.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * Base exception for Zuul exceptions.
 */
public class ZuulException extends NestedRuntimeException {

    /**
     * Construct a {@code ZuulException} with the specified
     * {@linkplain String#format(String, Object...) formatted string} as
     * a detail message.
     *
     * @param message The formatted message.
     * @param args Arguments referenced by the format specifiers in the
     *             formatted message.
     */
    public ZuulException(String message, Object... args) {
        super(String.format(message, args));
    }

    /**
     * Construct a {@code ZuulException} with the specified detail message and
     * nested exception.
     *
     * @param message The detail message.
     * @param cause The nested exception.
     */
    public ZuulException(String message, Throwable cause) {
        super(message, cause);
    }
}
