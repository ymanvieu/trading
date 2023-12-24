package fr.ymanvieu.trading.common.rate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import fr.ymanvieu.trading.common.symbol.Symbol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
public class Rate {

	private Symbol fromcur;
	private Symbol tocur;
	private BigDecimal value;
	private Instant date;

	@Override
	public int hashCode() {
		return Objects.hash(fromcur, tocur, date, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof Rate))
			return false;

		Rate other = (Rate) obj;

		return Objects.equals(fromcur, other.fromcur) //
				&& Objects.equals(tocur, other.tocur) //
				&& Objects.equals(date, other.date) //
				&& value.compareTo(other.value) == 0;
	}
}
