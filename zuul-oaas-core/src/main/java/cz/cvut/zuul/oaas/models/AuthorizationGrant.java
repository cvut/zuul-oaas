package cz.cvut.zuul.oaas.models;

public enum AuthorizationGrant {

    CLIENT_CREDENTIALS,
    IMPLICIT,
    AUTHORIZATION_CODE,
    RESOURCE_OWNER,
    REFRESH_TOKEN;


    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
