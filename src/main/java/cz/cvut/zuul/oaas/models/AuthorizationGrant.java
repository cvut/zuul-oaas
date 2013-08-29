package cz.cvut.zuul.oaas.models;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
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
