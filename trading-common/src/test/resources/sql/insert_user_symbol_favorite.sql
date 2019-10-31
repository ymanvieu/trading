insert into users (id,username,password) values 
(1,'test',''),
(2,'user','');

insert into symbols (code,name,country_flag,currency) values 
('EUR','Euro','europeanunion',null),
('USD','US Dollar','us',null),
('UBI','Ubisoft Entertainment SA',null,'EUR');

insert into favorite_symbol (from_symbol_code,to_symbol_code,username) values
('USD', 'EUR', 'test'),
('UBI', 'EUR', 'user');