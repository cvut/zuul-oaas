package cz.cvut.authserver.oauth2.models.resource;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class Auth {

    @JsonProperty("scopes")
    private List<Scope> scopes;

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScope(List<Scope> scope) {
        this.scopes = scope;
    }

    @JsonIgnore
    public boolean isSecured() {
        if (scopes==null) {
            return false;
        }
        for (Scope scope : scopes) {
            if (scope.isSecured()) {
                return true;
            }
        }
        return false;
    }
}
