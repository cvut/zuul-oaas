FORMAT: 1A

OAAS Management API v1
======================

This document describes RESTful API of the Zuul OAAS for clients, resources and tokens management in version v1. It’s written in the [API Blueprint format](http://apiblueprint.org/).


# Group Clients

This section groups **clients** resources.


## Client [/clients/{id}]

**Client** is a representation of application that requests some protected resources provided by **resource providers**. An application developer must register his client on OAAS to obtain *client_id* and *client_secret*.

+ Parameters
    + id (string) ... ID of the client in the form of UUID.

+ Model (application/json)

    ```json
    {
        "client_id": "e12b71c4-e31f-4bd5-80bc-a6e341214430",
        "client_secret": "change-me",
        "scope": [
            "urn:zuul:scope:sample.read",
            "urn:zuul:scope:sample.write"
        ],
        "resource_ids": [],
        "authorized_grant_types": [
            "authorization_code",
            "refresh_token"
        ],
        "redirect_uris": [ "http://example.org" ],
        "authorities": [ "ROLE_CLIENT" ],
        "access_token_validity": 3600,
        "refresh_token_validity": 15552000,
        "display_name": "Simple Client",
        "client_locked": false,
        "client_type": "android"
    }
    ```


### Retrieve a Client [GET]

+ Response 200

    [Client][]


### Update a Client [PUT]

To update an existing **client**, provide the updated client representation with _all_ attributes. You should request the actual representation instantly before updating it.

If you omit the *client_secret*, a new one will be generated (i.e. password reset).
The *client_id* cannot be changed – it must equals the path parameter, otherwise you will get *409 CONFLICT*.

+ Request

    [Client][]

+ Response 204


### Remove a Client [DELETE]

When you remove a Client, all associated tokens will be removed as well.

+ Response 204


## Clients Collection [/clients]

Collection of [Client][]s.


### Create a Client [POST]

To create a new Client provide a JSON object same as [Client][], but without *client_id* and *client_secret* (will be generated for you).

+ Request

    ```json
    {
        "scope": [
            "urn:zuul:scope:sample.read",
            "urn:zuul:scope:sample.write"
        ],
        "resource_ids": [],
        "authorized_grant_types": [
            "authorization_code",
            "refresh_token"
        ],
        "redirect_uris": [ "https://example.org" ],
        "authorities": [ "ROLE_CLIENT" ],
        "access_token_validity": 3600,
        "refresh_token_validity": 15552000,
        "display_name": "Simple Client",
        "client_type": "android"
    }
    ```

+ Response 201

    + Headers

            Location: /clients/42



# Group Resources

This section groups resources for managing **resource providers**.


## Resource [/resources/{id}]

**Resource provider** (or server) is a representation of the server hosting some protected resources that will be accessed by clients.

+ Parameters
    + id (string) ... ID of the resource.

+ Model (application/json)

    ```json
    {
        "resource_id": "sample-api-123",
        "base_url": "https://example.org",
        "description": "This is a sample resource provider secured with OAuth 2.0.",
        "name": "Sample Resource Provider",
        "version": "1.0",
        "visibility": "public",
        "auth": {
            "scopes": [
                {
                    "name": "urn:zuul:scope:sample.read",
                    "description": "read-only resources",
                    "secured": false
                },
                {
                    "name": "urn:zuul:scope:sample.write",
                    "description": "privileged read-write resources",
                    "secured": true
                }
            ]
        }
    }
    ```


### Retrieve a Resource [GET]

+ Response 200

    [Resource][]


### Update a Resource [PUT]

To update an existing **resource**, provide the updated resource representation with all attributes. You should request the actual representation instantly before updating it.

The *resource_id* cannot be changed – it must equals the path parameter, otherwise you will get *409 CONFLICT*.

+ Request

    [Resource][]

+ Response 204


### Remove a Resource [DELETE]

+ Response 204


## Resources Collection [/resources]

Collection of **resource providers**.


### Retrieve all Resources [GET]

+ Response 200

    ```json
    [
        {
            "resource_id": "sample-api-123",
            "base_url": "https://example.org",
            "description": "This is a sample resource provider secured with OAuth 2.0.",
            "name": "Sample Resource Provider",
            "version": "1.0",
            "visibility": "public",
            "auth": {
                "scopes": [
                    {
                        "name": "urn:zuul:scope:sample.read",
                        "description": "read-only resources",
                        "secured": false
                    },
                    {
                        "name": "urn:zuul:scope:sample.write",
                        "description": "privileged read-write resources",
                        "secured": true
                    }
                ]
            }
        }
    ]
    ```


### Create a Resource [POST]

To create a new **resource** provide a JSON object same as [Resource][], but without *resource_id* (will be generated for you).

+ Request

    ```json
    {
        "base_url": "https://example.org",
        "description": "This is a sample resource provider secured with OAuth 2.0.",
        "name": "Sample Resource Provider",
        "version": "1.0",
        "visibility": "public",
        "auth": {
            "scopes": [
                {
                    "name": "urn:zuul:scope:sample.read",
                    "description": "read-only resources",
                    "secured": false
                }
            ]
        }
    }
    ```

+ Response 201

    + Headers

        Location: /resources/sample-api-123


# Token [/tokens/{tokenValue}]

This resource provides an extra information about the assigned [access tokens](http://tools.ietf.org/html/rfc6749#section-1.4) and [refresh tokens](http://tools.ietf.org/html/rfc6749#section-1.5).

+ Parameters
    + tokenValue (string) ... UUID value of the token.

+ Model

    ```json
    {
        "expiration": "2013-12-11T10:09:08Z",
        "scope": [ "urn:zuul:scope:sample.read" ],
        "token_type": "bearer_type",
        "token_value": "a62b78c4-e32f-4bd5-80bc-a6e441144930",
        "client_authentication": {
            "client_id": "e12b71c4-e31f-4bd5-80bc-a6e341214430",
            "client_locked": false,
            "display_name": "Simple Client",
            "scope": [ "urn:zuul:scope:sample.read" ],
            "redirect_uris": "https://example.org",
            "resource_ids": []
        },
        "user_authentication": {
            "username": "flynn",
            "email": "kevin.flynn@encom.com",
            "first_name": "Kevin",
            "last_name": "Flynn"
        }
    }
    ```

## Retrieve a Token [GET]

+ Response 200

    [Token][]


## Invalidate a Token [DELETE]

+ Response 204
