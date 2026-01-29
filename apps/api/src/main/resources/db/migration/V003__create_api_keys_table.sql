CREATE TABLE IF NOT EXISTS portfolio.api_keys (
                        id uuid PRIMARY KEY,
                        key text NOT NULL UNIQUE,
                        client text NOT NULL,
                        scopes text[] NOT NULL DEFAULT '{}',
                        expires_at timestamptz NULL,
                        created_by text,
                        created_at timestamptz NOT NULL DEFAULT now(),
                        last_modified_by text NULL,
                        last_modified_at timestamptz NULL
);

CREATE INDEX IF NOT EXISTS api_keys_client_idx ON portfolio.api_keys (client);