package fr.ymanvieu.trading.common.admin;

import java.util.Objects;

import fr.ymanvieu.trading.common.provider.Quote;

public record PairInfo(Integer id, String code, String name, Quote quote) {

	public PairInfo {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(code, "code");
		Objects.requireNonNull(name, "name");
		//TODO add not null check for quote
	}
}
