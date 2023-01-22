package fr.ymanvieu.trading.webapp.rate.dto;

import java.math.BigDecimal;
import java.time.Instant;

import fr.ymanvieu.trading.webapp.symbol.dto.SymbolDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RateDTO {

	private Boolean favorite;
	private SymbolDTO fromcur;
	private SymbolDTO tocur;
	private BigDecimal value;
	private Instant	date;
}
