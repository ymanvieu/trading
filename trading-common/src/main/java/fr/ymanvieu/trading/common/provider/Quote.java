package fr.ymanvieu.trading.common.provider;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quote {

	private String code;
	private String currency;

	private BigDecimal price;
	private Instant time;

	public Quote(String code, BigDecimal price, Instant time) {
		this.code = code;
		this.price = price;
		this.time = time;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, currency, price, time);
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
