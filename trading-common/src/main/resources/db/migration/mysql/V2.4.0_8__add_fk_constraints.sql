ALTER TABLE `trading`.`assets` 
ADD INDEX `fk_assets_symbol_idx` (`symbol_code` ASC);
ALTER TABLE `trading`.`assets` 
ADD CONSTRAINT `fk_assets_symbol_code`
  FOREIGN KEY (`symbol_code`)
  REFERENCES `trading`.`symbols` (`code`)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;
  
  ALTER TABLE `trading`.`assets` 
ADD INDEX `fk_assets_portofolio_idx` (`portofolio_id` ASC);
ALTER TABLE `trading`.`assets` 
ADD CONSTRAINT `fk_assets_portofolio_id`
  FOREIGN KEY (`portofolio_id`)
  REFERENCES `trading`.`portofolio` (`id`)
  ON DELETE NO ACTION
  ON UPDATE CASCADE;

  
  ALTER TABLE `trading`.`symbols` 
ADD INDEX `fk_symbols_currency_idx` (`currency` ASC);
ALTER TABLE `trading`.`symbols` 
ADD CONSTRAINT `fk_symbols_currency`
  FOREIGN KEY (`currency`)
  REFERENCES `trading`.`symbols` (`code`)
  ON DELETE RESTRICT
  ON UPDATE CASCADE;
