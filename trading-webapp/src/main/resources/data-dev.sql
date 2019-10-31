INSERT INTO symbols (code,currency,name,country_flag) VALUES ('USD',NULL,'US Dollar','us');
INSERT INTO symbols (code,currency,name,country_flag) VALUES ('BTC','USD','Bitcoin',NULL);
INSERT INTO symbols (code,currency,name,country_flag) VALUES ('EUR',NULL,'Euro','europeanunion');
INSERT INTO symbols (code,currency,name,country_flag) VALUES ('GBP',NULL,'British Pound Sterling','gb');

insert into symbols (code,name,country_flag,currency) values 
('UBI','Ubisoft Entertainment SA',null,'EUR'),
('GFT','GameLoft S.E.',null,'EUR'),
('BZ=F','Brent Crude Oil Last Day Financfuture','oil','USD'),
('RR','Rolls Royce Holdings plc',null,'GBP'),
('FAKE', 'Fake data', null, 'EUR');

INSERT INTO PAIR (symbol,name,source,target,exchange,provider_code) VALUES
('UBI.PA','Ubisoft Entertainment SA','UBI','EUR',null,'YAHOO'),
('GFT.PA','GameLoft S.E.','GFT','EUR',null,'YAHOO'),
('RR.L','Rolls Royce Holdings plc','RR','GBP',null,'YAHOO'),
('BZ=F', 'Brent Crude Oil Last Day Financfuture', 'BZ=F', 'USD', 'NY Mercantile', 'YAHOO'),
('BTCUSD=X','Bitcoin','BTC','USD','CCY','YAHOO'),
('FAKE','Fake data','FAKE','EUR',null,'YAHOO');


insert into users (id,username,password) values 
-- admin/password
(0,'admin','$2a$10$yCmLcG1BJWWBs64M5GTqq.COJg8opwFiE.49L1hZ/GV2bB40UEalG'),
-- user/password
(1,'user','$2a$10$yCmLcG1BJWWBs64M5GTqq.COJg8opwFiE.49L1hZ/GV2bB40UEalG');

insert into authorities (username,authority) values 
('admin', 'ADMIN'),
('user', 'USER');

insert into portofolio (id,user_id,base_currency_code,amount,version) values 
(0,0,'EUR',18500,'0'),
(1,1,'EUR',100000,'0');

insert into assets (id,portofolio_id,symbol_code,quantity,currency_code,currency_amount,version) values 
(1,0,'USD',10000,'EUR',8821.06,'0'),
(2,0,'GBP',5000,'EUR',5000,'0'),
(3,0,'UBI',50,'EUR',3000,'0'),
(4,0,'GFT',100,'EUR',700,'0');


INSERT INTO LATESTRATES(DATE,FROMCUR,TOCUR,VALUE) VALUES 
('2016-04-04 13:08:00.0','GFT','EUR',7.45),
('2016-04-04 13:08:00.0','UBI','EUR',27.860001),
('2016-04-12 00:00:00.0','BZ=F','USD',42.7),
('2016-04-19 11:37:44.0','USD','EUR',0.882106),
('2016-04-22 08:16:45.0','USD','GBP', 0.6965),
('2016-04-21 17:35:28.0','RR','GBP', 692),
('2017-12-07 16:35:28.0','BTC','USD', 16949),
('2017-12-07 16:35:28.0','FAKE','EUR', 1);

INSERT INTO RATES(DATE,FROMCUR,TOCUR,VALUE) VALUES 
('2016-04-04 13:08:00.0','GFT','EUR',7.45),
('2016-04-04 13:08:00.0','UBI','EUR',27.860001),
('2016-04-12 00:00:00.0','BZ=F','USD',42.7),
('2016-04-19 11:37:44.0','USD','EUR',0.882106),
('2016-04-22 08:16:45.0','USD','GBP', 0.6965),
('2016-04-21 17:35:28.0','RR','GBP', 692),
('2014-03-03 00:00:00','GFT','EUR',7.6380),
('2015-04-06 00:00:00','GFT','EUR',4.7720),
('2015-09-28 00:00:00','GFT','EUR',3.2400),
('2015-10-12 00:00:00','GFT','EUR',4.1060),
('2016-01-18 00:00:00','GFT','EUR',4.9540),
('2016-02-01 00:00:00','GFT','EUR',4.9000),
('2016-02-08 14:45:13','GFT','EUR',4.72792207792208),
('2016-02-15 08:00:06','GFT','EUR',5.63996031746032),
('2016-02-22 08:32:09','GFT','EUR',6.75726708074534),
('2016-03-03 16:35:10','GFT','EUR',7.54639344262295),
('2016-03-07 08:00:18','GFT','EUR',7.40396313364055),
('2016-03-14 08:07:48','GFT','EUR',7.30913333333333),
('2016-03-21 08:00:05','GFT','EUR',7.32840707964602),
('2016-03-29 07:01:43','GFT','EUR',7.42126582278481),
('2016-04-04 07:00:15','GFT','EUR',7.39904191616766),
('2016-04-11 07:00:10','GFT','EUR',7.48019841269841),
('2016-04-18 07:00:29','GFT','EUR',7.47171875),
('2017-12-07 16:35:28.0','BTC','USD', 16949),
('2017-12-07 16:35:28.0','FAKE','EUR', 1);
