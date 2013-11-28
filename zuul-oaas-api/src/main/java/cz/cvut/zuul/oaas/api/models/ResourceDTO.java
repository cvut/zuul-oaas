package cz.cvut.zuul.oaas.api.models;

import cz.cvut.zuul.oaas.api.validators.ValidURI;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Data
public class ResourceDTO implements Serializable {

    private String resourceId;

    private Auth auth;

    @NotEmpty @Size(max=256)
    @ValidURI(scheme={"https", "http"})
    private String baseUrl;

    @Size(max=256)
    private String description;

    @NotEmpty @Size(max=256)
    private String name;

    @Size(max=256)
    private String version;

    @NotEmpty
    @Pattern(regexp="(public|hidden)", flags=CASE_INSENSITIVE)
    private String visibility;



    @Data
    public static class Auth implements Serializable {

        private List<Scope> scopes;
    }


    @Data
    public static class Scope implements Serializable {

        @Size(max=256)
        private String name;

        @Size(max=256)
        private String description;

        private boolean secured = false;
    }
}
