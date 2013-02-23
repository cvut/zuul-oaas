package cz.cvut.authserver.oauth2.models;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public enum AuthorizationGrants {

    client_credentials,
    implicit,
    auth_code,
    resource_owner,
    refresh_token;


    public static boolean contains(String name) {
        for (AuthorizationGrants type : values()) {
            if (type.toString().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
