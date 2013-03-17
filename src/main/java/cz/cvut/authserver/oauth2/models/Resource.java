package cz.cvut.authserver.oauth2.models;

import cz.cvut.authserver.oauth2.api.validators.EnumValue;
import cz.cvut.authserver.oauth2.api.validators.ValidURI;
import cz.cvut.authserver.oauth2.models.enums.Visibility;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;

/**
 * Represents resources in the CTU OAuth 2.0 authorization server.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@TypeAlias("Resource")
@Document(collection = "resources")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Resource implements Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @JsonProperty("resource_id")
    private String id;

    @JsonProperty("auth")
    private Auth auth;

    @NotNull @Size(max=256)
    @ValidURI(scheme={"https", "http"})
    @JsonProperty("base_url")
    private String baseUrl;
    
    @Size(max=256)
    @JsonProperty("description")
    private String description;
    
    @NotNull @Size(max=256)
    @JsonProperty("name")
    private String name;
    
    @NotNull @Size(max=256)
    @JsonProperty("version")
    private String version;
    
    @NotNull @Size(max=256)
    @JsonProperty("title")
    private String title;

    @Indexed
    @NotNull
    @EnumValue(Visibility.class)
    @JsonProperty("visibility")
    private String visibility = Visibility.PUBLIC.toString();



    public Resource() {
    }

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


    public Auth getAuth() {
        return auth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }


    public boolean isNew() {
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31).append(id).append(name).append(version).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Resource other = (Resource) obj;
        return new EqualsBuilder().append(id, other.id).append(name, other.name)
                .append(version, other.version)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name)
                .append("version", version).append("visibility", visibility)
                .toString();
    }


    public static abstract class fields {
        public static final String
                RESOURCE_ID = "resource_id",
                AUTH = "auth",
                BASE_URL = "base_url",
                DESCRIPTION = "description",
                NAME = "name",
                VERSION = "version",
                TITLE = "title",
                VISIBILITY = "visibility";
    }
    
}
