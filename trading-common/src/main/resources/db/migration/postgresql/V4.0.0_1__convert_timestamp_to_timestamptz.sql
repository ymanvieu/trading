SET timezone = 'UTC';

ALTER TABLE assets
ALTER COLUMN created_date TYPE TIMESTAMP WITH TIME ZONE,
ALTER COLUMN last_modified_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE latestrates
ALTER COLUMN date TYPE TIMESTAMP WITH TIME ZONE,
ALTER COLUMN created_date TYPE TIMESTAMP WITH TIME ZONE,
ALTER COLUMN last_modified_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE pair
ALTER COLUMN created_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE portofolio
ALTER COLUMN created_date TYPE TIMESTAMP WITH TIME ZONE,
ALTER COLUMN last_modified_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE rates
ALTER COLUMN date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE symbols
ALTER COLUMN created_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE users
ALTER COLUMN created_date TYPE TIMESTAMP WITH TIME ZONE,
ALTER COLUMN last_modified_date TYPE TIMESTAMP WITH TIME ZONE;