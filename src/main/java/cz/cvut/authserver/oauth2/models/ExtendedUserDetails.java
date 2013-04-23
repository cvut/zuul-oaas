package cz.cvut.authserver.oauth2.models;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

/**
 * Think of ExtendedUser as the adapter between user's details storage and what
 * Spring Security needs inside the SecurityContextHolder. Being a
 * representation of something from our own user's details storage, quite often
 * you will cast the UserDetails to the original object that your application
 * provided, so you can call business-specific methods (like getEmail(),
 * getEmployeeNumber() and so on).
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class ExtendedUserDetails extends org.springframework.security.core.userdetails.User {

    private String email;
    private String firstname;
    private String lastname;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public ExtendedUserDetails(String email, String firstname, String lastname, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

}
