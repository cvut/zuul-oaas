package cz.cvut.zuul.oaas.api.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Data
public class ClientAuthenticationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientId;

    private Boolean clientLocked;

    private String productName;

    private Set<String> scope;

    private String redirectUri;

    private Set<String> resourceIds;

}
