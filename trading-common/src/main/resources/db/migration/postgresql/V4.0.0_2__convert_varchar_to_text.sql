ALTER TABLE assets
ALTER COLUMN symbol_code TYPE TEXT,
ALTER COLUMN currency_code TYPE TEXT,
ALTER COLUMN created_by TYPE TEXT,
ALTER COLUMN last_modified_by TYPE TEXT;

ALTER TABLE authorities
ALTER COLUMN authority TYPE TEXT;

ALTER TABLE favorite_symbol
ALTER COLUMN from_symbol_code TYPE TEXT,
ALTER COLUMN to_symbol_code TYPE TEXT;

ALTER TABLE latestrates
ALTER COLUMN fromcur TYPE TEXT,
ALTER COLUMN tocur TYPE TEXT;

ALTER TABLE pair
ALTER COLUMN symbol TYPE TEXT,
ALTER COLUMN name TYPE TEXT,
ALTER COLUMN source TYPE TEXT,
ALTER COLUMN target TYPE TEXT,
ALTER COLUMN exchange TYPE TEXT,
ALTER COLUMN provider_code TYPE TEXT,
ALTER COLUMN created_by TYPE TEXT;

ALTER TABLE portofolio
ALTER COLUMN base_currency_code TYPE TEXT,
ALTER COLUMN created_by TYPE TEXT,
ALTER COLUMN last_modified_by TYPE TEXT;

ALTER TABLE symbols
ALTER COLUMN code TYPE TEXT,
ALTER COLUMN currency TYPE TEXT,
ALTER COLUMN name TYPE TEXT,
ALTER COLUMN country_flag TYPE TEXT,
ALTER COLUMN created_by TYPE TEXT;

ALTER TABLE users
ALTER COLUMN username TYPE TEXT,
ALTER COLUMN password TYPE TEXT,
ALTER COLUMN provider TYPE TEXT,
ALTER COLUMN provider_user_id TYPE TEXT,
ALTER COLUMN email TYPE TEXT;
