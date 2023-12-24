package fr.ymanvieu.trading.webapp.portofolio.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PortofolioDTO {

	private AssetDTO baseCurrency;

	private List<AssetDTO> assets;

	private double currentValue;
	private double percentChange;
	private double valueChange;
}
