package cz.cvut.zuul.oaas.oauth2;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

/**
 * Exception thrown when a client is locked.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ClientLockedException extends ClientAuthenticationException {

    /**
     * Construct a {@code ClientLockedException} with the specified
     * {@linkplain String#format(String, Object...) formatted string} as
     * a detail message.
     *
     * @param message The formatted message.
     * @param args Arguments referenced by the format specifiers in the
     *             formatted message.
     */
    public ClientLockedException(String message, Object... args) {
        super(String.format(message, args));
    }
  
    @Override
    public int getHttpErrorCode() {
        return 401;
    }

    public String getOAuth2ErrorCode() {
        return "locked_client";
    }
}
