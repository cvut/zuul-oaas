package cz.cvut.zuul.oaas.models;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Data
@TypeAlias("Auth")
public class Auth {

    //TODO should be saved as references
    private List<Scope> scopes;


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
