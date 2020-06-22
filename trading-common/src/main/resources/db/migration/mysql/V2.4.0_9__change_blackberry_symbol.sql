update rates set fromcur='BB' where fromcur='BBRY';
delete from latestrates where fromcur='BBRY';
update assets set currency_code='BB' where currency_code='BBRY';

delete from symbols where code='BBRY';