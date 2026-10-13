CREATE TABLE booking (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    user_id         BIGINT NOT NULL,
    event_id        BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL,
    total           NUMERIC(10, 2) NOT NULL,
    currency        VARCHAR(3) NOT NULL,
    hold_expires_at TIMESTAMPTZ NOT NULL,
    idempotency_key UUID NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT ck_booking_status CHECK ( status IN ('HELD', 'CONFIRMED', 'EXPIRED', 'CANCELLED')),
    CONSTRAINT uk_booking_idempotency UNIQUE (idempotency_key),
    CONSTRAINT ck_booking_total CHECK ( total >= 0 ),
    CONSTRAINT ck_booking_currency CHECK ( currency ~ '^[A-Z]{3}$' )
);

CREATE INDEX idx_booking_user ON booking (user_id);
CREATE INDEX idx_booking_event ON booking (event_id);

CREATE TABLE booking_item (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    booking_id      BIGINT NOT NULL,
    event_seat_id   BIGINT NOT NULL,
    price           NUMERIC(10,2) NOT NULL,
    currency        VARCHAR(3) NOT NULL,

    CONSTRAINT fk_booking_item_booking FOREIGN KEY (booking_id) REFERENCES booking (id),
    CONSTRAINT fk_booking_item_event_seat FOREIGN KEY (event_seat_id) REFERENCES event_seat (id),
    CONSTRAINT uk_booking_item_booking_seat UNIQUE (booking_id, event_seat_id),
    CONSTRAINT ck_booking_item_price CHECK ( price >= 0 ),
    CONSTRAINT ck_booking_item_currency CHECK ( currency ~ '^[A-Z]{3}$' )
);

CREATE INDEX idx_booking_item_event_seat ON booking_item (event_seat_id);