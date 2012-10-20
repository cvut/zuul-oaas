package cz.cvut.authserver.oauth2.api.models;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@JsonSerialize(include=Inclusion.NON_NULL)
public class SecretChangeRequest {

    private String oldSecret;
    private String newSecret;


    @JsonProperty("old_secret")
    public String getOldSecret() {
        return oldSecret;
    }

    public void setOldSecret(String oldSecret) {
        this.oldSecret = oldSecret;
    }

    @JsonProperty("new_secret")
    public String getNewSecret() {
        return newSecret;
    }

    public void setNewSecret(String newSecret) {
        this.newSecret = newSecret;
    }
}
