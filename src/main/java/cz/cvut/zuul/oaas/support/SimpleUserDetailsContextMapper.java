package cz.cvut.zuul.oaas.support;

import cz.cvut.zuul.oaas.models.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

import static org.springframework.util.CollectionUtils.mergeArrayIntoCollection;

/**
 * The context mapper used by the LDAP authentication provider to create an
 * LDAP user object of type {@link User}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Setter @Slf4j
public class SimpleUserDetailsContextMapper implements UserDetailsContextMapper {

    /**
     * The name of the LDAP attribute which contains the user's e-mail address.
     * Defaults to "mail".
     */
    private String emailAttrName = "mail";

    /**
     * The name of the LDAP attribute which contains the user's first name.
     * Defaults to "givenName".
     */
    private String firstNameAttrName = "givenName";

    /**
     * The name of the LDAP attribute which contains the user's last name.
     * Defaults to "sn".
     */
    private String lastNameAttrName = "sn";

    /**
     * The default roles that will by granted to any successfully authenticated
     * user. These will be converted to {@link GrantedAuthority}s and added to
     * the list in the returned LdapUserDetails object.
     */
    private String[] defaultRoles = new String[0];



    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {

        log.debug("Mapping user entry: {}", ctx.getNameInNamespace());

        String email = ctx.getStringAttribute(emailAttrName);
        String firstName = ctx.getStringAttribute(firstNameAttrName);
        String lastName = ctx.getStringAttribute(lastNameAttrName);

        mergeArrayIntoCollection(defaultRoles, authorities);

        return new User(username, email, firstName, lastName, authorities);
    }

    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException("This class supports only reading from a context.");
    }
}
