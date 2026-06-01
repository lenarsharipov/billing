CREATE TABLE IF NOT EXISTS subscriptions
(
    id               BIGSERIAL PRIMARY KEY,
    tariff_id        BIGINT       NOT NULL REFERENCES tariffs (id) ON DELETE RESTRICT,
    user_id          BIGINT       NOT NULL,
    state            VARCHAR(256) NOT NULL,
    activation_date  TIMESTAMPTZ  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);