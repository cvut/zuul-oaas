package cz.cvut.zuul.oaas.api.models;

import cz.cvut.zuul.oaas.api.validators.EnumValue;
import cz.cvut.zuul.oaas.api.validators.ValidURI;
import cz.cvut.zuul.oaas.models.enums.Visibility;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
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
    @EnumValue(Visibility.class)
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
