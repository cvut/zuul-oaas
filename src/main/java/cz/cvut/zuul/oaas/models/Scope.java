package cz.cvut.zuul.oaas.models;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Represents a scope of the access token issued by authorization server in
 * OAuth 2.0 protocol. Access token scopes are then used in the authorization
 * and token endpoints. The value of the scope parameter is expressed as a list
 * of space-delimited, case sensitive strings (%x21 / %x23-5B / %x5D-7E). The
 * strings are defined by the authorization server.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@TypeAlias("Scope")
@Document(collection = "scopes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scope implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Size(max=256)
    private String name;
    
    @Size(max=256)
    private String description;

    private boolean secured = false;


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
        return new HashCodeBuilder(3, 59).append(name).append(description).append(secured).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        Scope other = (Scope) obj;
        return new EqualsBuilder().append(name, other.name)
                .append(description, other.description).append(secured, other.secured)
                .isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("secured", secured).toString();
    }
    
}
