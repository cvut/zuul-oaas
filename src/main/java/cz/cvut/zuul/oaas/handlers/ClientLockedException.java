package cz.cvut.zuul.oaas.handlers;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

/**
 * Exception thrown when a client is locked.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ClientLockedException extends ClientAuthenticationException{

    public ClientLockedException(String msg) {
        super(msg);
    }
  
    @Override
    public int getHttpErrorCode() {
        return 401;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "locked_client";
    }
    
}
