package fr.ymanvieu.trading.common.portofolio;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import lombok.Value;

@Value
public class Order {

	SymbolEntity from;
	double quantity;
	SymbolEntity to;
	double value;
}
