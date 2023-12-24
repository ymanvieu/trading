package fr.ymanvieu.trading.common.provider;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record Quote(String code, String currency, BigDecimal price, Instant time) {

	public Quote {
		Objects.requireNonNull(code, "code");
		Objects.requireNonNull(currency, "currency");
		Objects.requireNonNull(price, "price");
		Objects.requireNonNull(time, "time");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof Quote other))
			return false;

		return Objects.equals(time,other.time) //
				&& Objects.equals(currency, other.currency) //
				&& Objects.equals(code, other.code) //
				&& price.compareTo(other.price) == 0;
	}
}
