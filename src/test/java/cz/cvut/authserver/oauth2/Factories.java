package cz.cvut.authserver.oauth2;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cz.cvut.authserver.oauth2.api.models.ClientDTO;
import cz.cvut.authserver.oauth2.models.Auth;
import cz.cvut.authserver.oauth2.models.Client;
import cz.cvut.authserver.oauth2.models.Resource;
import cz.cvut.authserver.oauth2.models.Scope;
import cz.cvut.authserver.oauth2.models.enums.AuthorizationGrant;
import cz.cvut.authserver.oauth2.models.enums.Visibility;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.provider.*;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;

import static java.util.Arrays.asList;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class Factories {

    private static final int MAX_STRING_LENGTH = 16;
    private static final int DEFAULT_LIST_SIZE = 2;


    //////// ClientDetails ////////
    
    public static ClientDetails createEmptyClientDetails(String clientId) {
        BaseClientDetails client = new BaseClientDetails(clientId, null, null, null, null, null);
        return client;
    }

    public static ClientDetails createRandomClientDetails(String clientId) {
        BaseClientDetails client = new BaseClientDetails();

        client.setAccessTokenValiditySeconds( randomInt() );
        client.setAdditionalInformation( randomStringsMap(2) );
        client.setAuthorities( randomGrantedAuthorities(2) );
        client.setAuthorizedGrantTypes( randomStringList(DEFAULT_LIST_SIZE) );
        client.setClientId( clientId );
        client.setClientSecret( randomString() );
        client.setRefreshTokenValiditySeconds( randomInt() );
        client.setRegisteredRedirectUri( new HashSet<>(randomStringList(DEFAULT_LIST_SIZE)) );
        client.setResourceIds( randomStringList(DEFAULT_LIST_SIZE) );
        client.setScope( randomStringList(DEFAULT_LIST_SIZE) );

        return client;
    }

    public static ClientDetails createRandomClientDetails() {
        return createRandomClientDetails(randomString());
    }


    //////// Client ////////

    public static Client createEmptyClient(String clientId) {
        Client client = new Client();
        client.setClientId(clientId);
        return client;
    }

    public static Client createRandomClient(String clientId) {
        Client client = new Client();

        client.setAccessTokenValiditySeconds( randomInt() );
        client.setAuthorities( randomGrantedAuthorities(2) );
        client.setAuthorizedGrantTypes( asList(AuthorizationGrant.AUTHORIZATION_CODE, AuthorizationGrant.REFRESH_TOKEN) );
        client.setClientId( clientId );
        client.setClientSecret( randomString() );
        client.setRefreshTokenValiditySeconds( randomInt() );
        client.setRegisteredRedirectUri( asList(URI.create("https://app.cvut.cz")) );
        client.setResourceIds( randomStringList(DEFAULT_LIST_SIZE) );
        client.setScope( randomStringList(DEFAULT_LIST_SIZE) );
        client.setProductName( randomString() );

        return client;
    }

    public static Client createRandomClient() {
        return createRandomClient( randomString() );
    }


    //////// ClientDTO ////////

    public static ClientDTO createEmptyClientDTO() {
        ClientDTO client = new ClientDTO();
        return client;
    }

    public static ClientDTO createRandomClientDTO(String clientId) {
        ClientDTO client = new ClientDTO();

        client.setAccessTokenValiditySeconds( randomInt() );
        client.setAuthorities( randomStringList(DEFAULT_LIST_SIZE) );
        client.setAuthorizedGrantTypes( randomStringList(DEFAULT_LIST_SIZE) );
        client.setClientId( clientId );
        client.setClientSecret( randomString() );
        client.setRefreshTokenValiditySeconds( randomInt() );
        client.setRegisteredRedirectUri(randomStringList(DEFAULT_LIST_SIZE));
        client.setResourceIds( randomStringList(DEFAULT_LIST_SIZE) );
        client.setScope( randomStringList(DEFAULT_LIST_SIZE) );

        return client;
    }

    public static ClientDTO createRandomClientDTO() {
        return createRandomClientDTO(randomString());
    }
    
    
    //////////  Authorization Grant  //////////

    public static Set<String> createInvalidAuthorizationGrants(){
        return Sets.newHashSet("very_bad", "evil_grant");
    }

    public static Set<String> createAuthorizationCodeGrant(){
        return Sets.newHashSet(AuthorizationGrant.AUTHORIZATION_CODE.toString());
    }

    public static Set<String> createImplicitGrant(){
        return Sets.newHashSet(AuthorizationGrant.IMPLICIT.toString());
    }

    public static Collection<String> createAllAuthorizationGrants() {
        return Lists.transform(asList(AuthorizationGrant.values()), new Function<AuthorizationGrant, String>() {
            public String apply(AuthorizationGrant input) {
                return input.toString();
            }
        });
    }
    
    

    //////// OAuth2AccessToken ////////

    public static OAuth2AccessToken createEmptyAccessToken(String value) {
        return new DefaultOAuth2AccessToken(value);
    }

    public static OAuth2AccessToken createEmptyAccessTokenWithRefreshToken(String accessToken, String refreshToken) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken);
        token.setRefreshToken(createRefreshToken(refreshToken));

        return token;
    }

    public static OAuth2AccessToken createRandomAccessToken(String value) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(value);

        token.setAdditionalInformation( randomStringsMap(2) );
        token.setExpiration( randomFutureDate() );
        token.setRefreshToken(null);
        token.setScope( new HashSet<>(randomStringList(2)) );
        token.setTokenType( OAuth2AccessToken.BEARER_TYPE );

        return token;
    }

    public static OAuth2AccessToken createRandomAccessToken() {
        return createRandomAccessToken( randomString() );
    }

    public static OAuth2AccessToken createExpiredAccessToken() {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) createRandomAccessToken();
        token.setExpiration( randomPastDate() );

        return token;
    }



    //////// OAuth2RefreshToken ////////

    public static OAuth2RefreshToken createRefreshToken(String value) {
        return new DefaultOAuth2RefreshToken(value);
    }

    public static ExpiringOAuth2RefreshToken createExpiringRefreshToken(String value, Date expiration) {
        return new DefaultExpiringOAuth2RefreshToken(value, expiration);
    }

    
    
    //////// OAuth2Authentication ////////

    public static OAuth2Authentication createEmptyOAuth2Authentication(String clientId) {
        return new OAuth2Authentication(createEmptyAuthorizationRequest(clientId), null);
    }

    public static OAuth2Authentication createRandomOAuth2Authentication(boolean clientOnly) {
        return new OAuth2Authentication(
                createRandomAuthorizationRequest(),
                clientOnly ? null : createUserAuthentication(randomString(), false));
    }



    //////// AuthorizationRequest ////////

    public static AuthorizationRequest createEmptyAuthorizationRequest(String clientId) {
        return new DefaultAuthorizationRequest(clientId, null);
    }

    public static AuthorizationRequest createRandomAuthorizationRequest() {
        DefaultAuthorizationRequest request;

        request = new DefaultAuthorizationRequest(randomAuthorizationParameters());
        request.setApprovalParameters( randomStringsMap(2) );
        request.setApproved( true );
        request.setAuthorities( randomGrantedAuthorities(2) );
        request.setResourceIds( new HashSet<>(randomStringList(2)) );

        return request;
    }



    //////// UserAuthentication ////////

    public static Authentication createUserAuthentication(String name, boolean authenticated) {
        return new StubAuthentication(name, randomGrantedAuthorities(2), authenticated);
    }



    //////////  Resources  //////////

    public static Resource createResource() {
        return createResource(null);
    }

    public static Resource createResource(String resourceId) {
        Auth auth = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.readonly", "Read only scope", false));
        return createResource(auth, resourceId, "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Basic");
    }

    private static Resource createResource(Auth auth, String code, String url, String desc, String name, String version, String title) {
        Resource resource = new Resource(auth, code, URI.create(url), desc, name, version, title, Visibility.PUBLIC);
        return resource;
    }

    private static Auth createAuth(Scope... scopes) {
        Auth auth = new Auth();
        auth.setScope(Arrays.asList(scopes));
        return auth;
    }

    private static Scope createScope(String name, String description, boolean secured) {
        return new Scope(name, description, secured);
    }


    
    //////// Support ////////
    
    public static String randomAsciiPrintableStringWithSize(int size){
         return RandomStringUtils.randomAscii(size);
    }

    private static Collection<GrantedAuthority> randomGrantedAuthorities(int size) {
        return AuthorityUtils.createAuthorityList(randomStringArray(size));
    }

    private static Map<String, String> randomAuthorizationParameters() {
         return new HashMap<String, String>(4) {{
                put(AuthorizationRequest.CLIENT_ID, randomString());
                put(AuthorizationRequest.SCOPE, StringUtils.arrayToCommaDelimitedString(randomStringArray(2)));
        }};
            }

    private static Map randomStringsMap(int size) {
        Map<String, String> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(randomString(), randomString());
        }
        return map;
    }

    private static int randomInt() {
        return RandomUtils.nextInt();
    }

    private static String randomString() {
        int length = RandomUtils.nextInt(MAX_STRING_LENGTH -1) + 1;
        return RandomStringUtils.randomAlphanumeric(length);
    }
    
    private static String[] randomStringArray(int size) {
        String[] array = new String[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = randomString();
        }
        return array;
    }

    private static List<String> randomStringList(int size) {
        return Arrays.asList(randomStringArray(size));
    }

    private static Date randomFutureDate() {
        Date now = new Date();
        int offset = randomInt();
        return new Date(now.getTime() + offset);
    }

    private static Date randomPastDate() {
        Date now = new Date();
        int offset = randomInt();
        return new Date(now.getTime() - offset);
    }



    //////// Stubs ////////

    private static class StubAuthentication extends AbstractAuthenticationToken {

        private String principal;
        private Collection<GrantedAuthority> authorities;

        public StubAuthentication(String name, boolean authenticated) {
            this(name, Collections.<GrantedAuthority>emptySet(), authenticated);
        }
        public StubAuthentication(String name, Collection<GrantedAuthority> authorities, boolean authenticated) {
            super(null);
            setAuthenticated(authenticated);
            this.principal = name;
            this.authorities = authorities;
        }

        public Object getCredentials() {
            return null;
        }

        public Object getPrincipal() {
            return principal;
        }

        @Override
        public Collection<GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(7, 97).append(principal).toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            StubAuthentication other = (StubAuthentication) obj;
            return new EqualsBuilder().append(this.principal, other.principal).isEquals();
        }
    }

}
