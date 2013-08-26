package cz.cvut.zuul.oaas.models;

import cz.cvut.zuul.oaas.api.validators.EnumValue;
import cz.cvut.zuul.oaas.api.validators.ValidURI;
import cz.cvut.zuul.oaas.models.enums.Visibility;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;

/**
 * Represents resources in the CTU OAuth 2.0 authorization server.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@ToString(of={"id", "name", "version"})

@TypeAlias("Resource")
@Document(collection = "resources")

@JsonIgnoreProperties(ignoreUnknown=true)

public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("resource_id")
    private String id;

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
    
    @NotEmpty @Size(max=256)
    private String title;

    @Indexed
    @NotEmpty @EnumValue(Visibility.class)
    private String visibility = Visibility.PUBLIC.toString();



    @Deprecated
    public Resource(Auth auth, String id, URI baseUrl, String description, String name, String version, String title, Visibility visibility) {
        this.auth = auth;
        this.id = id;
        this.baseUrl = baseUrl.toString();
        this.description = description;
        this.name = name;
        this.version = version;
        this.title = title;
        this.visibility = visibility.toString();
    }
}
