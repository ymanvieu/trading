alter table latestrates drop column id;

alter table latestrates drop key latestrates_from_to_uk;
alter table latestrates add primary key (`fromcur`, tocur);