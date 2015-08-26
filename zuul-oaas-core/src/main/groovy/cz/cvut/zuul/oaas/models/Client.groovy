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
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.ClientDetails

@TypeAlias('Client')
@Document(collection = 'clients')
@EqualsAndHashCode(includes = 'clientId')
@ToString(includes = ['clientId', 'displayName'], includePackage = false)
class Client implements ClientDetails {

    private static final long serialVersionUID = 2L

    @Id
    String clientId

    @Field('secret')
    String clientSecret

    @Field('scopes')
    Set<String> scope = new LinkedHashSet<>(0)

    @Field('resources')
    Set<String> resourceIds = new LinkedHashSet<>(0)

    @Field('grants')
    Set<String> authorizedGrantTypes = new LinkedHashSet<>(0)

    @Field('redirects')
    Set<String> registeredRedirectUri = new LinkedHashSet<>(0)

    @Field('authorities')
    Set<GrantedAuthority> authorities = new LinkedHashSet<>(0)

    @Field('acsTokenExp')
    Integer accessTokenValiditySeconds

    @Field('refTokenExp')
    Integer refreshTokenValiditySeconds

    @Field('name')
    String displayName

    @Field('locked')
    boolean locked = false


    Client() { }

    Client(ClientDetails prototype) {
        use(InvokerHelper) {
            this.properties = prototype.properties
        }
        def addl = prototype.additionalInformation ?: [:]
        displayName = addl.display_name
        locked = addl.locked
    }


    boolean isSecretRequired() {
        clientSecret != null
    }

    boolean isScoped() {
        scope?.empty
    }

    void setScope(Collection<String> scope) {
        this.scope = new LinkedHashSet(scope ?: [])
    }

    void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = new LinkedHashSet(resourceIds ?: [])
    }

    void setAuthorizedGrantTypes(Collection<AuthorizationGrant> authorizedGrantTypes) {
        this.authorizedGrantTypes = (authorizedGrantTypes*.toString() ?: []) as LinkedHashSet
    }

    void setRegisteredRedirectUri(Collection<String> redirectUris) {
        this.registeredRedirectUri = new LinkedHashSet(redirectUris ?: [])
    }

    void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = new LinkedHashSet(authorities ?: [])
    }

    // TODO implement properly
    boolean isAutoApprove(String scope) {
        false
    }

    Map<String, Object> getAdditionalInformation() {
        [
            display_name: displayName,
            locked: locked
        ]
    }
}
