package fr.ymanvieu.trading.common.portofolio;

import java.util.List;

import lombok.Value;

@Value
public class Portofolio {
	
	AssetInfo baseCurrency;
	List<AssetInfo> assets;
	
	double currentValue;
	double percentChange;
	double valueChange;
}
