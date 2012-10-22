package cz.cvut.authserver.oauth2.api.models;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.validation.BindingResult;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
public class JsonExceptionMapping {

    private final BindingResult bindingResult;
    private int status;
    private String message;
    private String moreInfo;
    
    public JsonExceptionMapping(BindingResult bindingResult, int status, String message){
        this.bindingResult = bindingResult;
        this.status = status;
        this.message = message;
        this.moreInfo = "not-provided";
    }

    @JsonProperty("status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("more_info")
    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }
    
    
}
