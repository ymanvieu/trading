package fr.ymanvieu.trading.common.portofolio;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class OrderInfo {

	AssetInfo selected;
	AssetInfo selectedCurrency;

	BigDecimal gainCost;
}
