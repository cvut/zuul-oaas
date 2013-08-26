package cz.cvut.zuul.oaas.api.models;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Data
public class TokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ClientAuthenticationDTO clientAuthentication;

    @JsonProperty("expiration")
    private Date expiration;

    private Set<String> scope;

    private String tokenType;

    private String tokenValue;

    private UserAuthenticationDTO userAuthentication;
}
