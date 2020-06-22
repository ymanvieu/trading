ALTER TABLE users CHANGE COLUMN `login` `username` VARCHAR(50) NOT NULL ;
ALTER TABLE `users` ADD COLUMN `enabled` INT(1) NOT NULL DEFAULT 1 AFTER `role`;

create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

create unique index ix_auth_username on authorities (username,authority);

insert into authorities (username,authority) (select username,role from users);

alter table users drop column role;
