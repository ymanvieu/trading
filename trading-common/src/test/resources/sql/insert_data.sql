insert into symbols (code,name,country_flag,currency) values 
('EUR','Euro','europeanunion',null),
('USD','US Dollar','us',null),
('GBP','British Pound Sterling','gb',null),
('BRE',null,null,'USD'),
('XAU','Gold','gold',null),
('XAG','Silver','silver',null),
('XAF','CFA Franc BEAC',null,null),
('RR','Rolls Royce',null,'GBP'),
('UBI','Ubisoft Entertainment SA',null,'EUR'),
('GFT','GameLoft S.E.',null,'EUR');

INSERT INTO PAIR (symbol,name,source,target,exchange,provider_code) VALUES
('UBI.PA','Ubisoft Entertainment SA','UBI','EUR','Paris','YAHOO'),
('GFT.PA','GameLoft S.E.','GFT','EUR',null,'YAHOO'),
('RR.L','Rolls Royce Holdings plc','RR','GBP','London', 'YAHOO');

INSERT INTO LATESTRATES(DATE, FROMCUR, TOCUR, VALUE) VALUES 
(TIMESTAMP '2020-03-12 15:10:00.0', 'RR', 'GBP', 150),
(TIMESTAMP '2020-03-12 16:35:00.0', 'UBI', 'EUR', 22.5),
(TIMESTAMP '2015-01-30 13:55:00.0', 'USD', 'EUR', 0.88),
(TIMESTAMP '2015-04-06 02:00:00.0', 'BRE', 'USD', 55.18),
(TIMESTAMP '2015-02-02 08:42:50.0', 'USD', 'GBP', 0.664982);


INSERT INTO RATES(DATE, FROMCUR, TOCUR, VALUE) VALUES
(TIMESTAMP '2015-01-30 22:47:39.0', 'USD', 'XAG', 0.058167),
(TIMESTAMP '2015-02-01 22:42:10.0', 'USD', 'XAF', 580.519165),
(TIMESTAMP '2015-02-01 22:42:10.0', 'USD', 'EUR', 0.883353),
(TIMESTAMP '2015-02-01 22:42:10.0', 'USD', 'USD', 1),

(TIMESTAMP '2015-02-02 08:41:00.0', 'USD', 'XAF', 579.646545),
(TIMESTAMP '2015-02-02 08:42:50.0', 'USD', 'EUR', 0.882044),
(TIMESTAMP '2015-02-02 08:41:00.0', 'USD', 'USD', 1),
(TIMESTAMP '2015-02-02 08:42:50.0', 'USD', 'GBP', 0.664982),
(TIMESTAMP '2015-02-02 08:41:55.0', 'USD', 'XAU', 0.000783),
(TIMESTAMP '2015-02-27 02:00:00.0', 'BRE', 'USD', 60.75),
(TIMESTAMP '2015-04-06 02:00:00.0', 'BRE', 'USD', 55.18);