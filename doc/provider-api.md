FORMAT: 1A

OAAS Providers API v1
=====================

This document describes RESTful API of the Zuul OAAS for resource providers to verify tokens. It’s written in the [API Blueprint format](http://apiblueprint.org/).


# TokenInfo [/tokeninfo]

**TokenInfo** is an simplified representation of an access token intended for resource providers to verify tokens.

+ Parameters
    + token (string) ... The access token to verify and get information about.

+ OAuth scope
    + urn:zuul:oauth:oaas:tokeninfo


## GET

The token representation is returned only when it’s valid. If it’s expired, locked or the owning client doesn’t exist anymore, then 409 is returned. When the token doesn’t exist at all, then 404 is returned.

+ Response 200 (application/json)

    ```json
    {
        "clientId": "e12b71c4-e31f-4bd5-80bc-a6e341214430",
        "scope": [ "urn:zuul:scope:quotes.read", "urn:zuul:scope:quotes.write" ],
        "audience": [ "quotes-provider" ],
        "client_authorities": [ "ROLE_CLIENT" ],
        "expires_in": 120,
        "user_id": "flynn",
        "user_email": "kevin.flynn@encom.com",
        "user_authorities": [ "ROLE_MASTER" ]
    }
    ```

+ Response 404

    ```json
    {
        "status": 404,
        "message": "Token was not recognised"
    }
    ```

+ Response 409

    ```json
    {
        "status": 409,
        "message": "Token has expired"
    }
    ```
