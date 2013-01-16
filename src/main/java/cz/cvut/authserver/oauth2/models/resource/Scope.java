package cz.cvut.authserver.oauth2.models.resource;

import java.io.Serializable;
import javax.validation.constraints.Size;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Represents a scope of the access token issued by authorization server in
 * OAuth 2.0 protocol. Access token scopes are then used in the authorization
 * and token endpoints. The value of the scope parameter is expressed as a list
 * of space-delimited, case sensitive strings (%x21 / %x23-5B / %x5D-7E). The
 * strings are defined by the authorization server.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scope implements Serializable{

    @Size(max=256)
    @JsonProperty("name")
    private String name;
    
    @Size(max=256)
    @JsonProperty("description")
    private String description;

    @JsonProperty("secured")
    private boolean secured;
    
    public Scope() {
    }

    public Scope(String name, String description, boolean secured) {
        this.name = name;
        this.description = description;
        this.secured = secured;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 59 * hash + (this.secured ? 1 : 0);
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
        final Scope other = (Scope) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Scope{" + "name=" + name + ", description=" + description + '}';
    }
    
}
