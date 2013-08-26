package cz.cvut.zuul.oaas.models;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ImplicitClientDetails {
    
    private String type;

    public ImplicitClientDetails() {
    }

    public ImplicitClientDetails(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
