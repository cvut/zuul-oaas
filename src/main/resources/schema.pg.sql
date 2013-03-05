CREATE TABLE clients (
  id                      VARCHAR(256) PRIMARY KEY,
  client_secret           VARCHAR(256),
  scope                   VARCHAR(256) ARRAY,
  resource_ids            VARCHAR(256) ARRAY,
  authorized_grant_types  VARCHAR(256) ARRAY,
  redirect_uri            VARCHAR(256) ARRAY,
  authorities             VARCHAR(256) ARRAY,
  access_token_validity   INTEGER,
  refresh_token_validity  INTEGER
);

CREATE TABLE refresh_tokens (
  id                  VARCHAR(256) PRIMARY KEY,
  expiration          TIMESTAMP,
  authentication      BYTEA
);

CREATE TABLE access_tokens (
  id                    VARCHAR(256) PRIMARY KEY,
  expiration            TIMESTAMP,
  token_type            VARCHAR(50),
  refresh_token         VARCHAR(256),
  scope                 VARCHAR(256) ARRAY,
  authentication        BYTEA,
  authentication_key    VARCHAR(256),
  client_id             VARCHAR(256),
  user_name             VARCHAR(256)
);

CREATE INDEX refresh_token_idx ON access_tokens (refresh_token);
CREATE INDEX authentication_key_idx ON access_tokens (authentication_key);
CREATE INDEX client_id_idx ON access_tokens (client_id);
CREATE INDEX user_name_idx ON access_tokens (user_name);
