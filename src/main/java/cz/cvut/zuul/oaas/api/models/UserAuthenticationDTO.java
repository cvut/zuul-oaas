package cz.cvut.zuul.oaas.api.models;

import lombok.Data;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Data
public class UserAuthenticationDTO {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

}
