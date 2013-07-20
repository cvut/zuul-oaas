package cz.cvut.zuul.oaas.models.enums;

import cz.cvut.zuul.oaas.utils.EnumUtils;

import java.util.Collection;
import java.util.List;

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


    public static List<AuthorizationGrant> valuesOf(Collection<String> names) {
        return EnumUtils.valuesOf(names, AuthorizationGrant.class);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
