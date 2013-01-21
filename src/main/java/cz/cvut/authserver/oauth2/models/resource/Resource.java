package cz.cvut.authserver.oauth2.models.resource;

import cz.cvut.authserver.oauth2.api.validators.constraint.ValidUrl;
import cz.cvut.authserver.oauth2.api.validators.constraint.ValidVisibility;
import cz.cvut.authserver.oauth2.models.resource.enums.ResourceVisibility;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

/**
 * Represents resources in the CTU OAuth 2.0 authorization server.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Resource {
    
    @JsonProperty("auth")
    private Auth auth;

    @JsonProperty("resourceId")
    private String id;

    @ValidUrl
    @Size(max=256)
    @JsonProperty("baserUrl")
    private String baseUrl;
    
    @Size(max=256)
    @JsonProperty("description")
    private String description;
    
    @NotNull @Size(max=256)
    @JsonProperty("name")
    private String name;
    
    @NotNull() @Size(max=256)
    @JsonProperty("version")
    private String version;
    
    @NotNull @Size(max=256)
    @JsonProperty("title")
    private String title;
    
    @NotNull @Size(max=256)
    @ValidVisibility
    @JsonProperty("visibility")
    private String visibility = ResourceVisibility.PUBLIC.get();

    public Resource() {
    }

    public Resource(Auth auth, String id, String baseUrl, String description, String name, String version, String title, String visibility) {
        this.auth = auth;
        this.id = id;
        this.baseUrl = baseUrl;
        this.description = description;
        this.name = name;
        this.version = version;
        this.title = title;
        this.visibility = visibility;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 31 * hash + (this.baseUrl != null ? this.baseUrl.hashCode() : 0);
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 31 * hash + (this.title != null ? this.title.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Resource other = (Resource) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Resource{" + "id=" + id + ", name=" + name + ", version=" + version + ", visibility=" + visibility + '}';
    }
    
}
