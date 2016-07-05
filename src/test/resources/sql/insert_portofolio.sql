insert into users (id,login,password) values 
(0,'toto',''),
(1,'seller',''),
(2,'user','');

insert into symbols (code,name,country_flag,currency) values 
('EUR','Euro','europeanunion',null),
('USD','US Dollar','us',null),
('UBI','Ubisoft Entertainment SA',null,'EUR'),
('BRE','Brent Crude Futures','oil','USD'),
('GBP','British Pound Sterling','gb',null),
('RR','Rolls Royce Holdings plc',null,'GBP'),
('MKS','Marks & Spencer Group plc',null,'GBP');

INSERT INTO PAIR (symbol,name,source,target,provider_code) VALUES
('UBI.PA','Ubisoft Entertainment SA','UBI','EUR','YAHOO'),
('MKS.L','Marks & Spencer Group plc','MKS','GBP','YAHOO'),
('RR.L','Rolls Royce Holdings plc','RR','GBP','YAHOO');


INSERT INTO LATESTRATES(DATE,FROMCUR,TOCUR,VALUE) VALUES 
('2016-03-30 17:35:24.0','UBI','EUR',28.155001),
('2016-04-12 00:00:00.0','BRE','USD',41.8),
('2016-04-19 11:37:44.0','USD','EUR',0.882106),
('2016-04-22 08:16:45.0','USD','GBP', 0.6965),
('2016-04-21 17:35:28.0','RR','GBP', 692),
('2016-04-22 17:29:44.0','MKS','GBP', 426.8);

INSERT INTO RATES(DATE,FROMCUR,TOCUR,VALUE) VALUES 
('2016-03-30 17:35:24.0','UBI','EUR',28.155001),
('2016-04-12 00:00:00.0','BRE','USD',41.8),
('2016-04-19 11:37:44.0','USD','EUR',0.882106),
('2016-04-22 08:16:45.0','USD','GBP', 0.6965),
('2016-04-21 17:35:28.0','RR','GBP', 692),
('2016-04-22 17:29:44.0','MKS','GBP', 426.8);

insert into assets (id, user_id,symbol_code,quantity,total_price,last_update) values 
(0,0,'EUR',2000,2000,'2016-03-30 19:28:00.0'),
(1,0,'GBP',5000,6000,'2016-04-22 18:37:00.0'),
(2,0,'MKS',10,4268,'2016-04-22 16:37:00.0'),
(3,1,'EUR',0,0,'2016-03-29 17:00:00.0'),
(4,1,'UBI',60,1800,'2016-03-29 17:00:00.0'),
(5,2,'EUR',0,0,'2016-03-29 17:00:00.0'),
(6,2,'UBI',100,3000,'2016-03-29 17:00:00.0'),
(7,2,'BRE',100,3000,'2016-04-11 20:31:00.0'),
(8,2,'GBP',1000,1200,'2016-04-20 08:00:00.0');

insert into portofolio (id, asset_id) values 
(0,0),
(1,3),
(2,5);