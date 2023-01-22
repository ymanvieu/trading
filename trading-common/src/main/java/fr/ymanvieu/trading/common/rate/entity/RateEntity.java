package fr.ymanvieu.trading.common.rate.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public abstract class RateEntity {

	protected SymbolEntity fromcur;
	protected SymbolEntity tocur;
	protected BigDecimal value;
	protected Instant date;

	public RateEntity(String from, String to, BigDecimal value, Instant date) {
		this(new SymbolEntity(from), new SymbolEntity(to), value, date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fromcur, tocur, date, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof RateEntity))
			return false;

		RateEntity other = (RateEntity) obj;

		return Objects.equals(fromcur, other.fromcur) //
				&& Objects.equals(tocur, other.tocur) //
				&& Objects.equals(date, other.date) //
				&& value.compareTo(other.value) == 0;
	}	
}
