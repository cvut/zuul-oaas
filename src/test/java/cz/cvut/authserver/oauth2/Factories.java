package cz.cvut.authserver.oauth2;

import com.google.common.collect.Sets;
import cz.cvut.authserver.oauth2.api.models.SecretChangeRequest;
import cz.cvut.authserver.oauth2.models.resource.Auth;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import cz.cvut.authserver.oauth2.models.resource.Scope;
import cz.cvut.authserver.oauth2.models.resource.enums.ResourceVisibility;
import cz.cvut.authserver.oauth2.utils.AuthorizationGrants;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;

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
    
    
    
    //////////  Authorization Grant  //////////

    public static Set<String> createInvalidAuthorizationGrant(){
        return Sets.newHashSet("very_bad");
    }

    public static Set<String> createAuthorizationCodeGrant(){
        return Sets.newHashSet(AuthorizationGrants.authCode.get());
    }

    public static Set<String> createImplicitGrant(){
        return Sets.newHashSet(AuthorizationGrants.implict.get());
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
        token.setTokenType( OAuth2AccessToken.OAUTH2_TYPE );

        return token;
    }

    public static OAuth2AccessToken createRandomAccessToken() {
        return createRandomAccessToken( randomString() );
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
        return new StubAuthentication(name, authenticated);
    }

    //////// SecretChangeRequest //////// 
    
    public static SecretChangeRequest createValidSecretChangeRequestForClient(ClientDetails random) {
        SecretChangeRequest request = new SecretChangeRequest();
        request.setNewSecret(randomString());
        request.setOldSecret(random.getClientSecret());
        return request;
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



    //////// Stubs ////////

    private static class StubAuthentication extends AbstractAuthenticationToken {

        private String principal;

        public StubAuthentication(String name, boolean authenticated) {
            super(null);
            setAuthenticated(authenticated);
            this.principal = name;
        }

        public Object getCredentials() {
            return null;
        }

        public Object getPrincipal() {
            return principal;
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
    
    //////////  Resources  //////////
    
    public static Resource createResources() {
        Auth auth = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.readonly", "Read only scope", false));
        return createResource(auth, null, "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Basic");
    }

    private static Resource createResource(Auth auth, String code, String url, String desc, String name, String version, String title) {
        Resource resource = new Resource(auth, code, url, desc, name, version, title, ResourceVisibility.PUBLIC.get());
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

}
