alter table latestrates
	add column created_date datetime,
	add column last_modified_date datetime;
	
alter table symbols 
	add column created_by varchar(50),
	add column created_date datetime;
	
alter table pair
	add column created_by varchar(50),
	add column created_date datetime;
	
alter table portofolio 
	add column created_by varchar(50),
	add column created_date datetime,
	add column last_modified_by varchar(50),
	add column last_modified_date datetime;
	
alter table assets 
	add column created_by varchar(50),
	add column created_date datetime,
	add column last_modified_by varchar(50),
	add column last_modified_date datetime;
	
alter table users 
	add column created_date datetime default current_timestamp;