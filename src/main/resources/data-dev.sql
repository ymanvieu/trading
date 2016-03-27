merge into symbols (code,name,country_flag,currency) values ('EUR','Euro','europeanunion',null);
merge into symbols (code,name,country_flag,currency) values ('UBI.PA','Ubisoft Entertainment SA',null,'EUR');

-- admin/password
merge into users (id,login,password) values (0, 'admin','$2a$10$yCmLcG1BJWWBs64M5GTqq.COJg8opwFiE.49L1hZ/GV2bB40UEalG');