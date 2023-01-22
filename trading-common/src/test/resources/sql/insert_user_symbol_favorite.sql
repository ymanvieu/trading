insert into users (id,username,provider) values
(1,'test','local'),
(2,'user','local');

insert into symbols (code,name,country_flag,currency) values 
('EUR','Euro','eu',null),
('USD','US Dollar','us',null),
('UBI','Ubisoft Entertainment SA',null,'EUR');

insert into favorite_symbol (from_symbol_code,to_symbol_code,user_id) values
('USD', 'EUR', 1),
('UBI', 'EUR', 2);