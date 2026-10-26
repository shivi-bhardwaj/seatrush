ALTER TABLE event ADD COLUMN currency VARCHAR(3);
UPDATE event SET currency = 'INR';
ALTER TABLE event ALTER COLUMN currency SET NOT NULL;
ALTER TABLE event ADD CONSTRAINT ck_event_currency CHECK (currency ~ '^[A-Z]{3}$');