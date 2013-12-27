package cz.cvut.zuul.oaas.config

import cz.cvut.zuul.oaas.support.SimpleUserDetailsContextMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager

@Configuration
@Profile('ldap')
class LdapUserAuthenticationConfig extends AbstractAuthenticationManagerConfig implements UserAuthenticationBeans {

    @Bean @Qualifier('user')
    AuthenticationManager userAuthenticationManager() {
        builder.ldapAuthentication().with {
            contextSource().url         $('auth.user.ldap.server.uri}') +'/'+ $('auth.user.ldap.server.base_dn')
            userDnPatterns              $('auth.user.ldap.user_dn_pattern')
            userSearchBase              $('auth.user.ldap.user_search_base')
            userSearchFilter            $('auth.user.ldap.user_search_filter')
            userDetailsContextMapper    ldapUserContextMapper()
        }.and().build()
    }

    @Bean ldapUserContextMapper() {
        new SimpleUserDetailsContextMapper (
            firstNameAttrName:  $('auth.user.ldap.attribute.fist_name'),
            lastNameAttrName:   $('auth.user.ldap.attribute.last_name'),
            emailAttrName:      $('auth.user.ldap.attribute.email'),
            defaultRoles:       'ROLE_USER'
        )
    }
}
