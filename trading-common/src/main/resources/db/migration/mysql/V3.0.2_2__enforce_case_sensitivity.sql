-- fix existing data
update symbols set currency = upper(currency) where currency = 'GBp';
update latestrates set tocur = upper(tocur) where tocur = 'GBp';
update pair set target = upper(target) where target = 'GBp';
update rates set tocur = upper(tocur) where tocur = 'GBp';

-- change charset/collation

/*
SELECT CONCAT("ALTER TABLE `",`TABLE_NAME`,"` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;") 
FROM `information_schema`.`TABLES` 
WHERE `TABLE_SCHEMA` = 'trading';

SELECT CONCAT("ALTER TABLE `",`TABLE_NAME`,"` MODIFY `",`COLUMN_NAME`,"` ",COLUMN_TYPE," CHARACTER SET latin1 COLLATE latin1_general_cs ",IF(`IS_NULLABLE`='YES', 'NULL', 'NOT NULL')," ",IF(`COLUMN_DEFAULT` IS NOT NULL, CONCAT(" DEFAULT '", `COLUMN_DEFAULT`, "'"), ''),";") 
FROM `information_schema`.`COLUMNS` 
WHERE `TABLE_SCHEMA` = 'trading' AND (`CHARACTER_SET_NAME` IS NOT NULL OR `COLLATION_NAME` IS NOT NULL);
*/


SET FOREIGN_KEY_CHECKS=0;
ALTER DATABASE trading DEFAULT CHARACTER SET latin1 DEFAULT COLLATE latin1_general_cs;

-- tables
ALTER TABLE `assets` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;           
ALTER TABLE `authorities` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;      
ALTER TABLE `favorite_symbol` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;  
ALTER TABLE `latestrates` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;      
ALTER TABLE `pair` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;             
ALTER TABLE `portofolio` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;       
ALTER TABLE `rates` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;            
ALTER TABLE `schema_version` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;   
ALTER TABLE `symbols` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;          
ALTER TABLE `users` DEFAULT CHARACTER SET latin1 COLLATE latin1_general_cs;   


ALTER TABLE `assets` MODIFY `symbol_code` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                               
ALTER TABLE `assets` MODIFY `currency_code` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                             
ALTER TABLE `assets` MODIFY `created_by` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                                   
ALTER TABLE `assets` MODIFY `last_modified_by` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                             
ALTER TABLE `authorities` MODIFY `username` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                            
ALTER TABLE `authorities` MODIFY `authority` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                           
ALTER TABLE `favorite_symbol` MODIFY `from_symbol_code` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                 
ALTER TABLE `favorite_symbol` MODIFY `to_symbol_code` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                   
ALTER TABLE `favorite_symbol` MODIFY `username` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                        
ALTER TABLE `latestrates` MODIFY `fromcur` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                              
ALTER TABLE `latestrates` MODIFY `tocur` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                
ALTER TABLE `pair` MODIFY `symbol` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                     
ALTER TABLE `pair` MODIFY `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                      
ALTER TABLE `pair` MODIFY `source` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                      
ALTER TABLE `pair` MODIFY `target` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                      
ALTER TABLE `pair` MODIFY `exchange` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                                       
ALTER TABLE `pair` MODIFY `provider_code` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                               
ALTER TABLE `pair` MODIFY `created_by` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                                     
ALTER TABLE `portofolio` MODIFY `base_currency_code` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                    
ALTER TABLE `portofolio` MODIFY `created_by` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                               
ALTER TABLE `portofolio` MODIFY `last_modified_by` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                         
ALTER TABLE `rates` MODIFY `fromcur` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                    
ALTER TABLE `rates` MODIFY `tocur` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                      
ALTER TABLE `schema_version` MODIFY `version` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                              
ALTER TABLE `schema_version` MODIFY `description` varchar(200) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                     
ALTER TABLE `schema_version` MODIFY `type` varchar(20) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                             
ALTER TABLE `schema_version` MODIFY `script` varchar(1000) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                         
ALTER TABLE `schema_version` MODIFY `installed_by` varchar(100) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                    
ALTER TABLE `symbols` MODIFY `code` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                     
ALTER TABLE `symbols` MODIFY `currency` varchar(8) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                                     
ALTER TABLE `symbols` MODIFY `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                                       
ALTER TABLE `symbols` MODIFY `country_flag` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                                
ALTER TABLE `symbols` MODIFY `created_by` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL ;                                  
ALTER TABLE `users` MODIFY `username` varchar(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                  
ALTER TABLE `users` MODIFY `password` varchar(64) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL ;                                  


SET FOREIGN_KEY_CHECKS=1;