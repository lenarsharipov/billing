CREATE TABLE IF NOT EXISTS outbox_events
(
    id               BIGSERIAL PRIMARY KEY,
    aggregate_type   VARCHAR(255) NOT NULL,
    aggregate_id     VARCHAR(255) NOT NULL,
    routing_key      VARCHAR(255) NOT NULL,
    payload          TEXT         NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    attempts         INT          NOT NULL DEFAULT 0,
    next_attempt_at  TIMESTAMPTZ  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);