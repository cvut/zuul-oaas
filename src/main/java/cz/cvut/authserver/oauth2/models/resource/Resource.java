package cz.cvut.authserver.oauth2.models.resource;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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
    private Long id;

    @JsonProperty("baserUrl")
    private String baseUrl;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("version")
    private String version;
    
    @JsonProperty("title")
    private String title;

    public Resource() {
    }

    public Resource(Auth auth, Long id, String baseUrl, String description, String name, String version, String title) {
        this.auth = auth;
        this.id = id;
        this.baseUrl = baseUrl;
        this.description = description;
        this.name = name;
        this.version = version;
        this.title = title;
    }

    public Auth getAuth() {
        return auth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return "Resource{" + "id=" + id + ", baseUrl=" + baseUrl + ", name=" + name + ", version=" + version + '}';
    }
    
}
