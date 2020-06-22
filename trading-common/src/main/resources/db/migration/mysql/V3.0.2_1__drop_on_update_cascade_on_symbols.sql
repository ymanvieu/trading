ALTER TABLE `assets` 
DROP FOREIGN KEY `fk_assets_portofolio_id`;
ALTER TABLE `assets` 
ADD CONSTRAINT `fk_assets_portofolio_id`
  FOREIGN KEY (`portofolio_id`)
  REFERENCES `portofolio` (`id`);

ALTER TABLE `assets` 
DROP FOREIGN KEY `fk_assets_symbol_code`;
ALTER TABLE `assets` 
ADD CONSTRAINT `fk_assets_symbol_code`
  FOREIGN KEY (`symbol_code`)
  REFERENCES `symbols` (`code`);
  
ALTER TABLE `assets` 
DROP FOREIGN KEY `assets_ibfk_1`;
ALTER TABLE `assets` 
ADD CONSTRAINT `fk_assets_currency_code`
  FOREIGN KEY (`currency_code`)
  REFERENCES `symbols` (`code`);

ALTER TABLE `latestrates` 
DROP FOREIGN KEY `fk_latestrates_fromcur`,
DROP FOREIGN KEY `fk_latestrates_tocur`;
ALTER TABLE `latestrates` 
ADD CONSTRAINT `fk_latestrates_fromcur`
  FOREIGN KEY (`fromcur`)
  REFERENCES `symbols` (`code`),
ADD CONSTRAINT `fk_latestrates_tocur`
  FOREIGN KEY (`tocur`)
  REFERENCES `symbols` (`code`);

ALTER TABLE `pair` 
DROP FOREIGN KEY `source_fk`,
DROP FOREIGN KEY `target_fk`;
ALTER TABLE `pair` 
ADD CONSTRAINT `fk_pair_source`
  FOREIGN KEY (`source`)
  REFERENCES `symbols` (`code`),
ADD CONSTRAINT `fk_pair_target`
  FOREIGN KEY (`target`)
  REFERENCES `symbols` (`code`);

ALTER TABLE `portofolio` 
DROP FOREIGN KEY `portofolio_ibfk_2`;
ALTER TABLE `portofolio` 
ADD CONSTRAINT `fk_portofolio_base_currency_code`
  FOREIGN KEY (`base_currency_code`)
  REFERENCES `symbols` (`code`);

ALTER TABLE `rates` 
DROP FOREIGN KEY `fk_rates_fromcur`,
DROP FOREIGN KEY `fk_rates_tocur`;
ALTER TABLE `rates` 
ADD CONSTRAINT `fk_rates_fromcur`
  FOREIGN KEY (`fromcur`)
  REFERENCES `symbols` (`code`),
ADD CONSTRAINT `fk_rates_tocur`
  FOREIGN KEY (`tocur`)
  REFERENCES `symbols` (`code`);
  
ALTER TABLE `symbols` 
DROP FOREIGN KEY `fk_symbols_currency`;
ALTER TABLE `symbols` 
ADD CONSTRAINT `fk_symbols_currency`
  FOREIGN KEY (`currency`)
  REFERENCES `symbols` (`code`);