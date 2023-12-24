package fr.ymanvieu.trading.common.provider.rate.yahoo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YahooHistoricalModel {

	private Chart chart;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Chart {

		private List<Result> result;

		@Data
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Result {

			private Meta meta;
			private List<Long> timestamp;
			private Indicators indicators;

			@Data
			@JsonIgnoreProperties(ignoreUnknown = true)
			public static class Meta {

				private String currency;
				private String symbol;
				private Double regularMarketPrice;
				private Long regularMarketTime;
				private Long firstTradeDate;
			}

			@Data
			@JsonIgnoreProperties(ignoreUnknown = true)
			public static class Indicators {

				private List<Quote> quote;

				@Data
				@JsonIgnoreProperties(ignoreUnknown = true)
				public static class Quote {

					private List<Double> open;
					private List<Double> close;
				}
			}

		}
	}
}
