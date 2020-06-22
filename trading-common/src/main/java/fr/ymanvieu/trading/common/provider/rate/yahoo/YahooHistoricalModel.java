/**
 * Copyright (C) 2017 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General private License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General private License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General private License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
				private String exchangeName;
				private String instrumentType;
				private Long firstTradeDate;
				private Long gmtoffset;
				private String timezone;
				private String exchangeTimezoneName;
				private String dataGranularity;
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
