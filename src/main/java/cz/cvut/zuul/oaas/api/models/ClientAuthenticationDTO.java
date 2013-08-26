package cz.cvut.zuul.oaas.api.models;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ClientAuthenticationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_locked")
    private Boolean clientLocked;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("scope")
    private Set<String> scope;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    @JsonProperty("resource_ids")
    private Set<String> resourceIds;



    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Boolean getClientLocked() {
        return clientLocked;
    }

    public void setClientLocked(Boolean clientLocked) {
        this.clientLocked = clientLocked;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
