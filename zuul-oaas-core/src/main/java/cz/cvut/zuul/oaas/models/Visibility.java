package cz.cvut.zuul.oaas.models;

/**
 * Value indicating Resource visibility
 */
public enum Visibility {
    
    PUBLIC,
    HIDDEN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
