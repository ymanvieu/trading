package fr.ymanvieu.trading.common.provider.rate.yahoo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YahooModel {

	private QuoteResponse quoteResponse;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class QuoteResponse {
		
		private List<Result> result;

		@Data
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Result {

			private String currency;
			private Double regularMarketPrice;
			private String exchangeTimezoneName;
			private String fullExchangeName;
			private String exchangeTimezoneShortName;
			private Long regularMarketTime;
			private Long gmtOffSetMilliseconds;
			private String exchange;
			private String symbol;
			private String shortName;
			private String longName;
		}
	}
}
