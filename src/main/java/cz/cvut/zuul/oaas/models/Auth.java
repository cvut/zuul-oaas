package cz.cvut.zuul.oaas.models;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@TypeAlias("Auth")
public class Auth {

    //TODO should be saved as references
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
