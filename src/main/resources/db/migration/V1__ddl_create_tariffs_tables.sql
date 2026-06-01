CREATE TABLE IF NOT EXISTS tariffs
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(256) UNIQUE NOT NULL,
    amount           NUMERIC(16, 2)      NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ         NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMPTZ         NOT NULL DEFAULT now()
);