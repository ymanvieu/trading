/**
 * Copyright (C) 2014 Yoann Manvieu
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