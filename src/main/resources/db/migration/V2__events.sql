CREATE TABLE event (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    organizer_id    BIGINT NOT NULL,
    venue_id        BIGINT NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    starts_at       TIMESTAMPTZ NOT NULL,
    sale_opens_at   TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL,
    status          VARCHAR(20) NOT NULL,

    CONSTRAINT fk_event_organizer FOREIGN KEY (organizer_id) REFERENCES users (id),
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES venue (id),
    CONSTRAINT ck_event_status CHECK ( status IN ('DRAFT', 'ON_SALE', 'SOLD_OUT', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT ck_event_sale_before_start CHECK ( sale_opens_at < starts_at )
);

CREATE INDEX idx_event_organizer ON event (organizer_id);
CREATE INDEX idx_event_venue ON event (venue_id);

CREATE TABLE price_tier (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id        BIGINT NOT NULL,
    name            VARCHAR(100) NOT NULL,
    price           NUMERIC(10,2) NOT NULL,
    currency        VARCHAR(3) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL,

    CONSTRAINT fk_price_tier_event FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT ck_price_tier_price CHECK ( price >= 0 ),
    CONSTRAINT uk_price_tier_event_name UNIQUE (event_id, name),
    CONSTRAINT ck_price_tier_currency CHECK ( currency ~ '^[A-Z]{3}$' )
);