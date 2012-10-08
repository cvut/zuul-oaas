package cz.cvut.authserver.oauth2.mongo;

/**
 * Names of collections and fields in Mongo database.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoDbConstants {

    /** Collections in database. */
    public static class collections {

        public static final String
                CLIENT_DETAILS = "client_details";

        private collections() { /* do not initialize */ }
    }

    /** Fields in collection client_details. */
    public static class client_details {

        public static final String
                CLIENT_ID = "_id",
                CLIENT_SECRET = "client_secret",
                SCOPE = "scope",
                RESOURCE_IDS = "resource_ids",
                AUTHORIZED_GRANT_TYPES = "authorized_grant_types",
                REDIRECT_URI = "redirect_uri",
                AUTHORITIES = "authorities",
                ACCESS_TOKEN_VALIDITY = "access_token_validity",
                REFRESH_TOKEN_VALIDITY = "refresh_token_validity",
                ADDITIONAL_INFORMATION = "additional_information";

        private client_details() { /* do not initialize */ }
    }

    private MongoDbConstants() { /* do not initialize */ }
}
