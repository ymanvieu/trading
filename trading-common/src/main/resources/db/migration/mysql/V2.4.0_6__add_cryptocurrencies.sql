--bitcoin
INSERT INTO pair (symbol,name,source,target,exchange,provider_code) VALUES ('BTCUSD=X','Bitcoin','BTC','USD','CCY','YAHOO');

--litecoin
insert into symbols (code,name,country_flag,currency) values ('LTC','Litecoin',null,'USD');
INSERT INTO pair (symbol,name,source,target,exchange,provider_code) VALUES ('LTCUSD=X','Litecoin','LTC','USD','CCY','YAHOO');

--ethereum
insert into symbols (code,name,country_flag,currency) values ('ETH','Ethereum',null,'USD');
INSERT INTO pair (symbol,name,source,target,exchange,provider_code) VALUES ('ETHUSD=X','Ethereum','ETH','USD','CCY','YAHOO');
