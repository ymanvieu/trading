package fr.ymanvieu.trading.webapp.portofolio.dto;

import fr.ymanvieu.trading.webapp.symbol.dto.SymbolDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AssetDTO {

	private SymbolDTO symbol;

	private SymbolDTO currency;

	private Double quantity;

	private Double value;

	private Double currentValue;

	private Double currentRate;

	
	private Double percentChange;

	
	private Double valueChange;
}
