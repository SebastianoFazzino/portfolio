
CREATE TABLE IF NOT EXISTS portfolio.knowledge_chunks (
                                                          id uuid PRIMARY KEY,
                                                          source text NOT NULL,
                                                          content text NOT NULL,
                                                          content_hash text NOT NULL UNIQUE,
                                                          embedding vector(384) NOT NULL,
                                                          created_by text,
                                                          created_at timestamptz NOT NULL DEFAULT now(),
                                                          last_modified_by text NULL,
                                                          last_modified_at timestamptz NULL
);

CREATE INDEX IF NOT EXISTS knowledge_chunks_source_idx
    ON portfolio.knowledge_chunks (source);
