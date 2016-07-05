insert into symbols (code,name,country_flag,currency) values 
('EUR','Euro','europeanunion',null),
('USD','US Dollar','us',null),
('UBI','Ubisoft Entertainment SA',null,'EUR'),
('GFT','GameLoft S.E.',null,'EUR'),
('BRE','Brent Crude Futures','oil','USD'),
('GBP','British Pound Sterling','gb',null),
('RR','Rolls Royce Holdings plc',null,'GBP');

INSERT INTO PAIR (symbol,name,source,target,provider_code) VALUES
('UBI.PA','Ubisoft Entertainment SA','UBI','EUR','YAHOO'),
('GFT.PA','GameLoft S.E.','GFT','EUR','YAHOO'),
('RR.L','Rolls Royce Holdings plc','RR','GBP','YAHOO');

insert into users (id,login,password,role) values 
-- admin/password
(0,'admin','$2a$10$yCmLcG1BJWWBs64M5GTqq.COJg8opwFiE.49L1hZ/GV2bB40UEalG','ADMIN'),
-- user/password
(1,'user','$2a$10$yCmLcG1BJWWBs64M5GTqq.COJg8opwFiE.49L1hZ/GV2bB40UEalG','USER');

insert into assets (id,user_id,symbol_code,quantity,total_price,last_update) values 
(0,0,'EUR',18500,18500,'2016-04-27 17:36:01.0'),
(1,0,'USD',10000,8821.06,'2016-03-01 18:00:01.0'),
(2,0,'GBP',5000,5000,'2016-04-21 18:00:01.0'),
(3,0,'UBI',50,3000,'2016-03-30 17:36:00.0'),
(4,0,'GFT',100,700,'2016-03-01 17:37:00.0'),
(5,1,'EUR',100000,100000,'2016-04-27 17:36:01.0');

insert into portofolio (id,asset_id) values 
(0,0),
(1,5);

INSERT INTO LATESTRATES(DATE,FROMCUR,TOCUR,VALUE) VALUES 
('2016-04-04 13:08:00.0','GFT','EUR',7.45),
('2016-04-04 13:08:00.0','UBI','EUR',27.860001),
('2016-04-12 00:00:00.0','BRE','USD',42.7),
('2016-04-19 11:37:44.0','USD','EUR',0.882106),
('2016-04-22 08:16:45.0','USD','GBP', 0.6965),
('2016-04-21 17:35:28.0','RR','GBP', 692);

INSERT INTO RATES(DATE,FROMCUR,TOCUR,VALUE) VALUES 
('2016-04-04 13:08:00.0','GFT','EUR',7.45),
('2016-04-04 13:08:00.0','UBI','EUR',27.860001),
('2016-04-12 00:00:00.0','BRE','USD',42.7),
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
('2016-04-18 07:00:29','GFT','EUR',7.47171875);
