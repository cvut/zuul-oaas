package cz.cvut.zuul.oaas.api.models;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.BaseClientDetails.ArrayOrStringDeserializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Authentication info about OAuth2 Token from remote authorization server.
 */
@Data
public class TokenInfo {

    private String clientId;

    @JsonDeserialize(using = ArrayOrStringDeserializer.class)
    private Set<String> scope = emptySet();

    //alias resource_ids
    @JsonDeserialize(using = ArrayOrStringDeserializer.class)
    private Set<String> audience = emptySet();

    @JsonProperty
    @JsonDeserialize(using = ArrayOrStringDeserializer.class)
    private Set<String> clientAuthorities = emptySet();

    private Integer expiresIn;

    private String userId;

    private String userEmail;

    @JsonProperty
    @JsonDeserialize(using = ArrayOrStringDeserializer.class)
    private Set<String> userAuthorities = emptySet();


    public boolean isClientOnly() {
        return userId == null;
    }

    @JsonIgnore
    public Set<GrantedAuthority> getUserAuthorities() {
        return toGrantedAuthorities(userAuthorities);
    }

    public void setUserAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.userAuthorities = AuthorityUtils.authorityListToSet(authorities);
    }

    @JsonIgnore
    public Set<GrantedAuthority> getClientAuthorities() {
        return toGrantedAuthorities(clientAuthorities);
    }

    public void setClientAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.clientAuthorities = AuthorityUtils.authorityListToSet(authorities);
    }

    private Set<GrantedAuthority> toGrantedAuthorities(Collection<String> authorities) {
        Set<GrantedAuthority> result = new HashSet<>();
        for (String authority : authorities) {
            result.add(new SimpleGrantedAuthority(authority));
        }
        return result;
    }
}
