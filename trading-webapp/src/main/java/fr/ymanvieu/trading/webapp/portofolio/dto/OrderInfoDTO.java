package fr.ymanvieu.trading.webapp.portofolio.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfoDTO {

	private AssetDTO selected;
	private AssetDTO selectedCurrency;
	private BigDecimal gainCost;	
}
