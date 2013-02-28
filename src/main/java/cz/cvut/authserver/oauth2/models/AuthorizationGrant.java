package cz.cvut.authserver.oauth2.models;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public enum AuthorizationGrant {

    CLIENT_CREDENTIALS,
    IMPLICIT,
    AUTH_CODE,
    RESOURCE_OWNER,
    REFRESH_TOKEN;


    public static boolean contains(String name) {
        for (AuthorizationGrant type : values()) {
            if (type.toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
