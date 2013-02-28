package cz.cvut.authserver.oauth2.api.models;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Deprecated
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
public class JsonExceptionMapping {

    private int status;
    private String message;
    private String moreInfo;
    
    public JsonExceptionMapping(int status, String message){
        this.status = status;
        this.message = message;
        this.moreInfo = "not-provided";
    }

    public JsonExceptionMapping(int status, String message, String moreInfo){
        this.status = status;
        this.message = message;
        this.moreInfo = moreInfo;
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
