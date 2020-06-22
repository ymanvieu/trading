create table favorite_symbol (
	username varchar(50),
	symbol_code varchar(8),
	constraint pk_favorite_symbol PRIMARY KEY(username, symbol_code),
	constraint fk_favorite_symbol_users foreign key(username) references users(username),
	constraint fk_favorite_symbol_smyobls foreign key(symbol_code) references symbols(code)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;