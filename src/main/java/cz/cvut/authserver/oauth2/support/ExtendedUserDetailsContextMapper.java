package cz.cvut.authserver.oauth2.support;

import cz.cvut.authserver.oauth2.models.ExtendedUserDetails;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyResponseControl;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.util.Assert;

/**
 * The context mapper used by the LDAP authentication provider to create an LDAP user object.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ExtendedUserDetailsContextMapper extends LdapUserDetailsMapper implements UserDetailsContextMapper {

    public static final Logger LOG = LoggerFactory.getLogger(ExtendedUserDetailsContextMapper.class);
    private String passwordAttributeName = "userPassword";
    private String emailAttributeName = "mail";
    private String firstnameAttributeName = "givenName";
    private String lastnameAttributeName = "sn";
    private String[] roleAttributes = null;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        String dn = ctx.getNameInNamespace();

        LOG.debug("Mapping user details from context with DN: " + dn);

        LdapUserDetailsImpl.Essence essence = new LdapUserDetailsImpl.Essence();
        essence.setDn(dn);

        Object passwordValue = ctx.getObjectAttribute(passwordAttributeName);

        if (passwordValue != null) {
            essence.setPassword(mapPassword(passwordValue));
        }

        essence.setUsername(username);

        // Map the roles
        for (int i = 0; (roleAttributes != null) && (i < roleAttributes.length); i++) {
            String[] rolesForAttribute = ctx.getStringAttributes(roleAttributes[i]);

            if (rolesForAttribute == null) {
                LOG.debug("Couldn't read role attribute '" + roleAttributes[i] + "' for user " + dn);
                continue;
            }

            for (String role : rolesForAttribute) {
                GrantedAuthority authority = createAuthority(role);

                if (authority != null) {
                    essence.addAuthority(authority);
                }
            }
        }

        // Add the supplied authorities

        for (GrantedAuthority authority : authorities) {
            essence.addAuthority(authority);
        }
        //FIXME TODO
        essence.addAuthority(new SimpleGrantedAuthority("ROLE_USER"));

        // Check for PPolicy data

        PasswordPolicyResponseControl ppolicy = (PasswordPolicyResponseControl) ctx.getObjectAttribute(PasswordPolicyControl.OID);

        if (ppolicy != null) {
            essence.setTimeBeforeExpiration(ppolicy.getTimeBeforeExpiration());
            essence.setGraceLoginsRemaining(ppolicy.getGraceLoginsRemaining());
        }
        

        String email = ctx.getStringAttribute(emailAttributeName);
        String firstname = ctx.getStringAttribute(firstnameAttributeName);
        String lastname = ctx.getStringAttribute(lastnameAttributeName);
        
        LdapUserDetails createdUserDetails = essence.createUserDetails();
        
        ExtendedUserDetails user = 
                new ExtendedUserDetails(email, firstname, lastname, createdUserDetails.getUsername(), "[protected]", createdUserDetails.getAuthorities());
        
        return user;
    }
    
    //////////  Accessors  //////////
    
    /**
     * The name of the attribute which contains the user's password. Defaults to
     * "userPassword".
     *
     * @param passwordAttributeName the name of the attribute
     */
    public void setPasswordAttributeName(String passwordAttributeName) {
        this.passwordAttributeName = passwordAttributeName;
    }

    /**
     * The names of any attributes in the user's  entry which represent application
     * roles. These will be converted to <tt>GrantedAuthority</tt>s and added to the
     * list in the returned LdapUserDetails object. The attribute values must be Strings by default.
     *
     * @param roleAttributes the names of the role attributes.
     */
    public void setRoleAttributes(String[] roleAttributes) {
        Assert.notNull(roleAttributes, "roleAttributes array cannot be null");
        this.roleAttributes = roleAttributes;
    }

    /**
     * The name of the attribute which contains the user's email address.
     * Defaults to "mail".
     *
     * @param emailAttributeName the name of the attribute
     */
    public void setEmailAttributeName(String emailAttributeName) {
        this.emailAttributeName = emailAttributeName;
    }

    /**
     * The name of the attribute which contains the user's first name.
     * Defaults to "givenName".
     * 
     * @param firstnameAttributeName 
     */
    public void setFirstnameAttributeName(String firstnameAttributeName) {
        this.firstnameAttributeName = firstnameAttributeName;
    }

    /**
     * The name of the attribute which contains the user's last name.
     * Defaults to "sn".
     * 
     * @param lastnameAttributeName 
     */
    public void setLastnameAttributeName(String lastnameAttributeName) {
        this.lastnameAttributeName = lastnameAttributeName;
    }

    
}
