package cz.cvut.authserver.oauth2.dao.mongo.converters;

/**
 * Names of collections and fields in Mongo database.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public final class MongoDbConstants {

    /** Collections in database. */
    public static final class collections {

        public static final String
                CLIENT_DETAILS = "client_details",
                ACCESS_TOKENS = "access_tokens",
                REFRESH_TOKENS = "refresh_tokens";

        private collections() { /* do not initialize */ }
    }

    /** Fields in collection client_details. */
    public static final class client_details {

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

    /** Fields in collection oauth_access_token */
    public static final class access_tokens {

        public static final String
                TOKEN_ID = "_id",
                EXPIRATION = "expiration",
                TOKEN_TYPE = "token_type",
                REFRESH_TOKEN = "refresh_token",
                SCOPE = "scope",
                ADDITIONAL_INFORMATION = "additional_information",
                AUTHENTICATION_KEY = "authentication_key",
                AUTHENTICATION = "authentication",
                CLIENT_ID = "client_id",
                USER_NAME = "user_name";

        private access_tokens() { /* do not initialize */ }
    }

    /** Fields in collection oauth_refresh_token */
    public static final class refresh_tokens {

        public static final String
                TOKEN_ID = "_id",
                EXPIRATION = "expiration",
                AUTHENTICATION = "authentication";

        private refresh_tokens() { /* do not initialize */ }
    }

    public static final class authentication {

        public static final String
                AUTHORIZATION_REQUEST = "authorization_request",
                USER_AUTHENTICATION = "user_authentication";

        private authentication() { /* do not initialize */ }
    }

    public static final class authz_request {

        public static final String
                APPROVAL_PARAMS = "approval_parameters",
                APPROVED = "approved",
                AUTHORITIES = "authorities",
                AUTHZ_PARAMS = "authorization_parameters",
                RESOURCE_IDS = "resource_ids";

        private authz_request() { /* do not initialize */ }
    }

    public static final class user_auth {

        //TODO incomplete!
        public static final String
                USER_NAME = "user_name",
                USER_EMAIL = "user_email",
                AUTHORITIES = "authorities";

        private user_auth() { /* do not initialize */ }
    }
    

    private MongoDbConstants() { /* do not initialize */ }
}
