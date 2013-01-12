package cz.cvut.authserver.oauth2.models.resource;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class Auth {

    @JsonProperty("scope")
    private List<Scope> scopes;

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScope(List<Scope> scope) {
        this.scopes = scope;
    }

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
