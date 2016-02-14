CREATE TABLE if not exists latestrates (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `fromcur` varchar(8) NOT NULL,
  `tocur` varchar(3) NOT NULL,
  `value` decimal(20,10) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE if not exists rates (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `fromcur` varchar(8) NOT NULL,
  `tocur` varchar(3) NOT NULL,
  `value` decimal(20,10) NOT NULL,
  PRIMARY KEY (`id`,`tocur`)
);


CREATE TABLE if not exists symbols (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(8) NOT NULL,
  `currency` varchar(3) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

create table if not exists users ( 
id integer auto_increment primary key,
login VARCHAR(64) unique not null, 
password VARCHAR(64) not null,
role varchar(32) not null default 'USER'
);