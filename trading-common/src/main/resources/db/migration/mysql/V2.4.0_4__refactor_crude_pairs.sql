-- BRENT
update symbols set code='BZ=F',name='Brent Crude Oil Last Day Financfuture' where code='BRE';

update pair set symbol='BZ=F',name='Brent Crude Oil Last Day Financfuture',exchange='NY Mercantile',provider_code='YAHOO' where symbol='BRE';


-- WTI
insert into symbols (code,name,country_flag,currency) values ('CL=F','Light Sweet Crude Oil Futures,Ofuture','oil','USD');
INSERT INTO `pair` (`symbol`,`name`,`source`,`target`,`exchange`,`provider_code`) VALUES ('CL=F','Light Sweet Crude Oil Futures,Ofuture','CL=F','USD','NY Mercantile','YAHOO');