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
package cz.cvut.zuul.oaas.api.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.Canonical
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.provider.client.Jackson2ArrayOrStringDeserializer

import static org.springframework.security.core.authority.AuthorityUtils.authorityListToSet

/**
 * Authentication info about OAuth2 Token from remote authorization server.
 */
@Canonical
class TokenInfo {

    String clientId

    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer)
    Set<String> scope = []

    //alias resource_ids
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer)
    Set<String> audience = []

    @JsonProperty
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer)
    Set<String> clientAuthorities = []

    Integer expiresIn

    String userId

    String userEmail

    @JsonProperty
    @JsonDeserialize(using = Jackson2ArrayOrStringDeserializer)
    Set<String> userAuthorities = []


    boolean isClientOnly() {
        userId == null
    }

    @JsonIgnore
    Set<GrantedAuthority> getUserAuthorities() {
        toGrantedAuthorities(userAuthorities)
    }

    void setUserAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.userAuthorities = authorityListToSet(authorities)
    }

    @JsonIgnore
    Set<GrantedAuthority> getClientAuthorities() {
        toGrantedAuthorities(clientAuthorities)
    }

    void setClientAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.clientAuthorities = authorityListToSet(authorities)
    }

    private Set<GrantedAuthority> toGrantedAuthorities(Collection<String> authorities) {
        authorities.collect { new SimpleGrantedAuthority(it) }.toSet()
    }
}
