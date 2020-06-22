-- add exchange in pair
ALTER TABLE pair ADD COLUMN `exchange` VARCHAR(45) NULL AFTER target;