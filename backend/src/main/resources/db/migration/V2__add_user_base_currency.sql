ALTER TABLE users
    ADD COLUMN base_currency CHAR(3) NOT NULL DEFAULT 'COP';

ALTER TABLE users
    ADD CONSTRAINT users_base_currency_check CHECK (base_currency ~ '^[A-Z]{3}$');

ALTER TABLE users
    ALTER COLUMN base_currency DROP DEFAULT;
