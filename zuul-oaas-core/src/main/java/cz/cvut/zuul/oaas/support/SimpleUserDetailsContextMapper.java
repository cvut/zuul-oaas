/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.support;

import cz.cvut.zuul.oaas.models.User;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.NONE;

/**
 * The context mapper used by the LDAP authentication provider to create an
 * LDAP user object of type {@link User}.
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

    @Setter(NONE)
    private List<GrantedAuthority> defaultAuthorities = emptyList();

    /**
     * The default roles that will by granted to any successfully authenticated
     * user. These will be converted to {@link GrantedAuthority}s and added to
     * set of authorities in the returned LdapUserDetails object.
     */
    public void setDefaultRoles(String[] defaultRoles) {
        if (defaultRoles != null) {
            this.defaultAuthorities = AuthorityUtils.createAuthorityList(defaultRoles);
        }
    }


    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {

        log.debug("Mapping user entry: {}", ctx.getNameInNamespace());

        String email = ctx.getStringAttribute(emailAttrName);
        String firstName = ctx.getStringAttribute(firstNameAttrName);
        String lastName = ctx.getStringAttribute(lastNameAttrName);

        Set<GrantedAuthority> mergedAuthorities = new LinkedHashSet<>(authorities);
        mergedAuthorities.addAll(defaultAuthorities);

        return new User(username, email, firstName, lastName, mergedAuthorities);
    }

    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException("This class supports only reading from a context.");
    }
}
