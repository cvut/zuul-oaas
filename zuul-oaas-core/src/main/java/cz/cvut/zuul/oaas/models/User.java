package cz.cvut.zuul.oaas.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of="username")
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Collection<? extends GrantedAuthority> authorities;


    public User(String username, String email, Collection<? extends GrantedAuthority> authorities) {
        this(username, email, null, null, authorities);
    }


    public Collection<GrantedAuthority> getAuthorities() {
        return unmodifiableCollection(authorities);
    }

    public String getPassword() {
        return "[empty]";
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
