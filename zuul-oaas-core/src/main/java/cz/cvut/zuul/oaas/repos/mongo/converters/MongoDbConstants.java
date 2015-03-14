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
package cz.cvut.zuul.oaas.repos.mongo.converters;

/**
 * Names of collections and fields in Mongo database.
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
                AUTHORIZATION_REQUEST = "authzReq",
                USER_AUTHENTICATION = "userAuth";

        private authentication() { /* do not initialize */ }
    }

    public static final class oauth_request {

        public static final String
                APPROVED = "approved",
                AUTHORITIES = "authorities",
                CLIENT_ID = "client",
                EXTENSIONS = "exts",
                REDIRECT_URI = "redirect",
                REQUEST_PARAMS = "reqParams",
                RESOURCE_IDS = "resources",
                RESPONSE_TYPES = "respTypes",
                SCOPE = "scope";

        private oauth_request() { /* do not initialize */ }
    }

    public static final class user_auth {

        //TODO incomplete!
        public static final String
                USER_NAME = "uname",
                USER_EMAIL = "email",
                AUTHORITIES = "authorities";

        private user_auth() { /* do not initialize */ }
    }


    private MongoDbConstants() { /* do not initialize */ }
}
