insert into symbols (code,name,country_flag,currency) values 
('EUR','Euro','eu',null),
('USD','US Dollar','us',null),
('BRE',null,null,null);

INSERT INTO rates(DATE, FROMCUR, TOCUR, VALUE) VALUES
(TIMESTAMP '2015-02-01 00:15:00.0', 'USD', 'EUR', 0.802),
(TIMESTAMP '2015-02-01 23:59:10.0', 'USD', 'EUR', 0.9),

(TIMESTAMP '2015-02-10 00:15:00.0', 'BRE', 'USD', 55.24),
(TIMESTAMP '2015-02-10 12:00:00.0', 'BRE', 'USD', 48.7),
(TIMESTAMP '2015-02-11 00:05:00.0', 'BRE', 'USD', 50.1),
(TIMESTAMP '2015-02-11 23:59:00.0', 'BRE', 'USD', 52.7),

(TIMESTAMP '2015-02-12 00:47:00.0', 'BRE', 'USD', 50),
(TIMESTAMP '2015-02-12 00:59:00.0', 'BRE', 'USD', 47.2),
(TIMESTAMP '2015-02-12 22:05:00.0', 'BRE', 'USD', 48.4),
(TIMESTAMP '2015-02-12 22:49:00.0', 'BRE', 'USD', 48.6),
(TIMESTAMP '2015-02-12 23:59:00.0', 'BRE', 'USD', 50.6),

(TIMESTAMP '2016-03-21 00:03:00.0', 'BRE', 'USD', 57),
(TIMESTAMP '2016-03-27 23:37:00.0', 'BRE', 'USD', 48),



(TIMESTAMP '2016-10-04 00:15:00.0', 'BRE', 'USD', 55.24),
(TIMESTAMP '2016-10-04 12:00:00.0', 'BRE', 'USD', 48.7),
(TIMESTAMP '2016-10-05 00:05:00.0', 'BRE', 'USD', 50.1),
(TIMESTAMP '2016-10-05 23:59:00.0', 'BRE', 'USD', 52.7),

(TIMESTAMP '2016-10-06 00:47:00.0', 'BRE', 'USD', 50),
(TIMESTAMP '2016-10-06 00:59:00.0', 'BRE', 'USD', 47.2),
(TIMESTAMP '2016-10-06 22:05:00.0', 'BRE', 'USD', 48.4),
(TIMESTAMP '2016-10-06 22:49:00.0', 'BRE', 'USD', 48.6),
(TIMESTAMP '2016-10-06 23:59:00.0', 'BRE', 'USD', 50.6),

(TIMESTAMP '2018-11-06 00:03:00.0', 'BRE', 'USD', 57),
(TIMESTAMP '2018-11-10 23:37:00.0', 'BRE', 'USD', 48);