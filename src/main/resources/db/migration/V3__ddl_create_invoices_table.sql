CREATE TABLE IF NOT EXISTS invoices
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    subscription_id  BIGINT      NOT NULL REFERENCES subscriptions (id),
    invoice_date     TIMESTAMPTZ NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMPTZ NOT NULL DEFAULT now()
);