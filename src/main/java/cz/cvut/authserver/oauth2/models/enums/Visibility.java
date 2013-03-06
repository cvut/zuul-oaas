package cz.cvut.authserver.oauth2.models.enums;

/**
 * Value indicating Resource visibility
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public enum Visibility {
    
    PUBLIC,
    HIDDEN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
