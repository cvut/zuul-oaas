package cz.cvut.authserver.oauth2.models.resource.enums;

/**
 * Value indicating Resource visibility
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public enum ResourceVisibility {
    
    PUBLIC("public"), HIDDEN("hidden");
    
    private String value;

    private ResourceVisibility(String value) {
        this.value = value;
    }
    
    public String get(){
        return value;
    }
    
}
