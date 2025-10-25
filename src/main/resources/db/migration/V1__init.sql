CREATE TABLE IF NOT EXISTS permissions (
                                           id UUID PRIMARY KEY,
                                           snippet_id UUID NOT NULL,
                                           user_id UUID NOT NULL,
                                           type VARCHAR(12) NOT NULL CHECK (type IN ('OWNER','SHARED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE UNIQUE INDEX IF NOT EXISTS uq_permissions_snippet_user
    ON permissions (snippet_id, user_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_permissions_owner_per_snippet
    ON permissions (snippet_id)
    WHERE type = 'OWNER';
