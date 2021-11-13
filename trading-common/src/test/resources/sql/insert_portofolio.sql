insert into users (id,username,provider) values
(0,'toto','local'),
(1,'seller','local'),
(2,'user','local');

insert into symbols (code,name,country_flag,currency) values 
('EUR','Euro','eu',null),
('USD','US Dollar','us',null),
('UBI','Ubisoft Entertainment SA',null,'EUR'),
('BRE','Brent Crude Futures','oil','USD'),
('GBP','British Pound Sterling','gb',null),
('RR','Rolls Royce Holdings plc',null,'GBP'),
('MKS','Marks & Spencer Group plc',null,'GBP'),
('RDSB','Royal Dutch Shell plc',null,'GBP')
;

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
('2016-04-22 17:29:44.0','MKS','GBP', 426.8),
('2019-04-08 13:48:44.0','RDSB','GBP', 2512)
;

insert into portofolio (id,user_id,base_currency_code,amount,version) values 
(0,0,'EUR',2000,1),
(1,1,'EUR',0,1),
(2,2,'EUR',0,1);

insert into assets (id,portofolio_id,symbol_code,quantity,currency_code,currency_amount,version) values 
(1,0,'GBP',5000,'EUR',6000,1),
(2,0,'MKS',10,'GBP',4268,1),
(4,1,'UBI',60,'EUR',1800,1),
(6,2,'UBI',100,'EUR',3000,1),
(7,2,'BRE',100,'USD',3000,1),
(8,2,'GBP',1000,'EUR',1200,1);