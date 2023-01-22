package fr.ymanvieu.trading.common.portofolio;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AssetInfo {

	private final SymbolEntity symbol;
	private final SymbolEntity currency;

	private Double quantity;
	private Double value;
	private Double currentValue;
	private Double currentRate;
	private Double percentChange;
	private Double valueChange;

	public AssetInfo(SymbolEntity symbol, SymbolEntity currency, double quantity) {
		this.symbol = symbol;
		this.currency = currency;
		this.quantity = quantity;
	}
}
