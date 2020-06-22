alter table rates drop column id;

alter table rates drop key rates_from_to_date_uk;
alter table rates add primary key (`fromcur`, tocur, `date`);