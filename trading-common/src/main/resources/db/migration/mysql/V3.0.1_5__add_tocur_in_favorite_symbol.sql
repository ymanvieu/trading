drop table favorite_symbol;

create table favorite_symbol (
	from_symbol_code varchar(8),
	to_symbol_code varchar(8),
	username varchar(50),
	constraint pk_favorite_symbol PRIMARY KEY(username, from_symbol_code, to_symbol_code),
	constraint fk_favorite_symbol_users foreign key(username) references users(username),
	constraint fk_favorite_symbol_from_symbol foreign key(from_symbol_code) references symbols(code),
	constraint fk_favorite_symbol_to_symbol foreign key(to_symbol_code) references symbols(code)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;