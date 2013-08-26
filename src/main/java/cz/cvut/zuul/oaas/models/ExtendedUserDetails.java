package cz.cvut.zuul.oaas.models;

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
    private String firstName;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ExtendedUserDetails(String email, String firstName, String lastName, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
