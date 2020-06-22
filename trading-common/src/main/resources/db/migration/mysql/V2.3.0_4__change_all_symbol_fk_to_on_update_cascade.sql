ALTER TABLE `assets` 
DROP FOREIGN KEY `assets_ibfk_1`;
ALTER TABLE `assets` 
ADD CONSTRAINT `assets_ibfk_1`
  FOREIGN KEY (`currency_code`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE;

  ALTER TABLE `latestrates` 
DROP FOREIGN KEY `fk_latestrates_fromcur`,
DROP FOREIGN KEY `fk_latestrates_tocur`;
ALTER TABLE `latestrates` 
ADD CONSTRAINT `fk_latestrates_fromcur`
  FOREIGN KEY (`fromcur`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE,
ADD CONSTRAINT `fk_latestrates_tocur`
  FOREIGN KEY (`tocur`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE;

  ALTER TABLE `pair` 
DROP FOREIGN KEY `source_fk`,
DROP FOREIGN KEY `target_fk`;
ALTER TABLE `pair` 
ADD CONSTRAINT `source_fk`
  FOREIGN KEY (`source`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE,
ADD CONSTRAINT `target_fk`
  FOREIGN KEY (`target`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE;

  ALTER TABLE `portofolio` 
DROP FOREIGN KEY `portofolio_ibfk_2`;
ALTER TABLE `portofolio` 
ADD CONSTRAINT `portofolio_ibfk_2`
  FOREIGN KEY (`base_currency_code`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE;

  ALTER TABLE `rates` 
DROP FOREIGN KEY `fk_rates_fromcur`,
DROP FOREIGN KEY `fk_rates_tocur`;
ALTER TABLE `rates` 
ADD CONSTRAINT `fk_rates_fromcur`
  FOREIGN KEY (`fromcur`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE,
ADD CONSTRAINT `fk_rates_tocur`
  FOREIGN KEY (`tocur`)
  REFERENCES `symbols` (`code`)
  ON UPDATE CASCADE;