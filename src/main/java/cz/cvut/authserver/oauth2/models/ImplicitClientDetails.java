package cz.cvut.authserver.oauth2.models;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ImplicitClientDetails {
    
    @JsonProperty("type")
    private String type;

    public ImplicitClientDetails() {
    }

    public ImplicitClientDetails(String type) {
        this.type = type;
    }
    
}
