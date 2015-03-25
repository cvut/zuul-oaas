/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.models

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import static java.util.Collections.unmodifiableCollection

@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'username', includePackage = false)
class User implements UserDetails {

    private static final long serialVersionUID = 2L

    String username
    String email
    String firstName
    String lastName
    Collection<? extends GrantedAuthority> authorities

    String password = '[empty]'
    boolean accountNonExpired = true
    boolean accountNonLocked = true
    boolean credentialsNonExpired = true
    boolean enabled = true


    void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = unmodifiableCollection(authorities)
    }
}
