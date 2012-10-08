package cz.cvut.authserver.oauth2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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


    
    //////// Support ////////

    private static Collection<GrantedAuthority> randomGrantedAuthorities(int size) {
        return AuthorityUtils.createAuthorityList(randomStringArray(size));
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

}
