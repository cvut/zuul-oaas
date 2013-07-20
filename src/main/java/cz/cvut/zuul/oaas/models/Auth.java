package cz.cvut.zuul.oaas.models;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.TypeAlias;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@TypeAlias("Auth")
public class Auth {

    //TODO should be saved as references
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
