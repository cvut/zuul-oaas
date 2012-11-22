package cz.cvut.authserver.oauth2.generators;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public interface OAuth2ClientCredentialsGenerator {

    public String generateClientId();

    public String generateClientSecret();
}
