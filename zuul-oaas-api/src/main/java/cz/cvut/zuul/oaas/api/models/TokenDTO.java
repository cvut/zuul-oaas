package cz.cvut.zuul.oaas.api.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Data
public class TokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date expiration;
    private ClientAuthentication clientAuthentication;
    private Set<String> scope;
    private String tokenType;
    private String tokenValue;
    private UserAuthentication userAuthentication;


    @Data
    public static class ClientAuthentication implements Serializable {

        private static final long serialVersionUID = 1L;

        private String clientId;
        private Boolean clientLocked;
        private String productName;
        private Set<String> scope;
        private String redirectUri;
        private Set<String> resourceIds;
    }


    @Data
    public static class UserAuthentication implements Serializable {

        private static final long serialVersionUID = 1L;

        private String username;
        private String email;
        private String firstName;
        private String lastName;
    }
}
