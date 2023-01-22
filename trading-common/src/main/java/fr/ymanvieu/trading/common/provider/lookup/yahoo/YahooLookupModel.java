package fr.ymanvieu.trading.common.provider.lookup.yahoo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YahooLookupModel {

	private List<Item> items;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Item {

		private String symbol;
		private String name;
		private String exch;
		private String type;
		private String exchDisp;
		private String typeDisp;

	}
}
