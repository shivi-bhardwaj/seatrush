CREATE TABLE event_seat (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL,
    event_id        BIGINT NOT NULL,
    seat_id         BIGINT NOT NULL,
    price_tier_id   BIGINT NOT NULL,
    status          VARCHAR(20) NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_event_seat UNIQUE (event_id, seat_id),
    CONSTRAINT ck_event_seat_status CHECK ( status IN ('AVAILABLE', 'HELD', 'BOOKED') ),
    CONSTRAINT fk_event_seat_event FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT fk_event_seat_seat FOREIGN KEY (seat_id) REFERENCES seat(id),
    CONSTRAINT fk_event_seat_price_tier FOREIGN KEY (price_tier_id) REFERENCES price_tier (id)
);

CREATE INDEX idx_event_seat_seat ON event_seat (seat_id);
CREATE INDEX idx_event_seat_price_tier ON event_seat (price_tier_id);