-- Initial schema for PostgreSQL

-----------------------------------------------------------
-- functions

create function update_updated_at() returns trigger as $$
  begin
    new.updated_at = now();
    return new;
  end;
$$ language plpgsql;

comment on function update_updated_at() is
  'This procedure is intended for use in a BEFORE UPDATE trigger to update a timestamp in the'
  ' updated_at column to the current time.';


-----------------------------------------------------------
-- types

-- Maps to enum AuthorizationGrant.
create type grant_type as enum (
  'client_credentials',
  'implicit',
  'authorization_code',
  'resource_owner',
  'refresh_token'
);

create type token_type as enum (
  'bearer'
);


-----------------------------------------------------------
-- table "templates"

create temporary table base (
  id          text       primary key,
  created_at  timestamp  not null default now(),
  updated_at  timestamp  not null default now()
);

comment on column base.created_at is
  'When the record was created. This column is populated automatically by a constraint.';
comment on column base.updated_at is
  'When the record was last modified. This column is updated automatically by a trigger.';


create temporary table authenticated (
  client_id       text  not null, --references clients
  user_id         text,
  authentication  json  not null
);

comment on column authenticated.client_id is
  'The value is copied from the authentication object on application level.'
  ' It is used only for querying and DB admin convenience.';
comment on column authenticated.user_id is
  'The value is copied from the authentication object on application level.'
  ' It is used only for querying and DB admin convenience.';
comment on column authenticated.authentication is
  'An OAuth 2 authentication serialized to a JSON object.';


-----------------------------------------------------------
-- Resource

create table resources (
  like base including all,

  description  text,
  name         text,
  base_url     text,
  scopes       json   not null default '{}',
  version      text,
  visibility   text   not null
);

create trigger resources_updated_at
  before update on resources
  for each row execute procedure update_updated_at();


-----------------------------------------------------------
-- Client

create table clients (
  like base including all,

  secret                  text          not null,
  scopes                  text[]        not null default '{}',
  resource_ids            text[]        not null default '{}',
  grant_types             grant_type[]  not null default '{}',
  redirect_uris           text[]        not null default '{}',
  authorities             text[]        not null default '{}',
  access_token_validity   integer       check (access_token_validity > 0 or access_token_validity is null),
  refresh_token_validity  integer       check (refresh_token_validity >= 0 or refresh_token_validity is null),
  display_name            text,
  locked                  boolean       not null default false,
  user_approval_required  boolean       not null default true
);

comment on column clients.id is
  'The client_id as defined in OAuth 2.0 specification.';
comment on column clients.secret is
  'The client_secret as defined in OAuth 2.0 specification.';
comment on column clients.scopes is
  'An array of scopes that the client is allowed to use. It must be sorted in alphabetical order.';
comment on column clients.resource_ids is
  'An array of resources that the client is allowed to access. They should exists in the resources'
  ' table and must be sorted in alphabetical order.';
comment on column clients.grant_types is
  'An array of the OAuth 2 grant types for which this client is authorized.'
  ' It must be sorted in alphabetical order.';
comment on column clients.redirect_uris is
  'An array of redirect URIs that the client can use during the authorization_code access grant.'
  ' It must be sorted in alphabetical order.';
comment on column clients.authorities is
  'An array of authorities that are granted to the client. It must be sorted in alphabetical order.';
comment on column clients.access_token_validity is
  'The access token validity period in seconds. NULL for default value set by token service.';
comment on column clients.refresh_token_validity is
  'The refresh token validity period in seconds. NULL for default value set by token service, and'
	' zero for non-expiring tokens.';
comment on column clients.display_name is 'The client name to be displayed to users.';

create trigger clients_updated_at
  before update on clients
  for each row execute procedure update_updated_at();


-----------------------------------------------------------
-- PersistableRefreshToken

create table refresh_tokens (
  like base including all,

  expires_at  timestamp  not null,

  like authenticated including all
);

comment on column refresh_tokens.id is
  'The token value; should be a valid UUID.';
comment on column refresh_tokens.expires_at is
  'When the token expires; after that date the token is subject to deletion.';

create index refresh_tokens_client_id_idx on refresh_tokens(client_id);
create index refresh_tokens_user_id_idx on refresh_tokens(user_id);

create trigger refresh_tokens_updated_at
  before update on refresh_tokens
  for each row execute procedure update_updated_at();


-----------------------------------------------------------
-- PersistableAccessToken

create table access_tokens (
  like base including all,

  expires_at      timestamp   not null,
  refresh_token   text,       --references refresh_tokens
  scopes          text[]      not null default '{}',
  token_type      token_type  not null,
  extra_data      json        not null default '{}',
  auth_key        text        not null,

  like authenticated including all
);

comment on column access_tokens.id is
  'The token value; should be a valid UUID.';
comment on column access_tokens.expires_at is
  'When the token expires; after that date the token is subject to deletion.';
comment on column access_tokens.refresh_token is
  'The refresh token (UUID) which can be used to obtain new access tokens.';
comment on column access_tokens.scopes is
  'An array of authorized scopes. It must be sorted in alphabetical order.';
comment on column access_tokens.extra_data is
  'A JSON object with any additional information used by extensions.';
comment on column access_tokens.auth_key is
  'The value is derived from client_id, username and scope fields of the authentication object.'
  ' Note that authentication''s scope is different than the scopes column!';

create index access_tokens_expires_at_idx on access_tokens(expires_at);
create index access_tokens_refresh_token_idx on access_tokens(refresh_token);
create index access_tokens_client_id_idx on access_tokens(client_id);
create index access_tokens_user_id_idx on access_tokens(user_id);
create index access_tokens_auth_key_idx on access_tokens(auth_key);

create trigger access_tokens_updated_at
  before update on access_tokens
  for each row execute procedure update_updated_at();


-----------------------------------------------------------
-- PersistableAuthorizationCode

create table authorization_codes (
  like base including all,
  like authenticated including all
);

comment on column authorization_codes.id is 'The authorization code.';

create trigger authorization_codes_updated_at
  before update on authorization_codes
  for each row execute procedure update_updated_at();


-----------------------------------------------------------
-- PersistableApproval

create table approvals (
  like base including all,

  client_id   text       not null, --references clients
  user_id     text       not null,
  scope       text       not null,
  approved    boolean    not null default false,
  expires_at  timestamp  not null,

  constraint natural_key unique (client_id, user_id, scope)
);

comment on column approvals.id is
  'This key is derived from client_id, user_id and scope on application level.'
  ' It is used only to simplify mapping.';

create index approvals_client_id_user_id_idx on approvals(client_id, user_id);

create trigger approvals_updated_at
  before update on approvals
  for each row execute procedure update_updated_at();
