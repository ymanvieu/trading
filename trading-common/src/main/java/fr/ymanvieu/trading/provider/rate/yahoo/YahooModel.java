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
package fr.ymanvieu.trading.provider.rate.yahoo;

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

			private String language;
			private String quoteType;
			private String currency;
			private Double ask;
			private Double regularMarketChangePercent;
			private Double regularMarketPreviousClose;
			private Double bid;
			private Double regularMarketPrice;
			private Double regularMarketChange;
			private Double regularMarketOpen;
			private Double regularMarketDayHigh;
			private Double regularMarketDayLow;
			private Long priceHLong;
			private String exchangeTimezoneName;
			private Long askSize;
			private String fullExchangeName;
			private Long bidSize;
			private String marketState;
			private Long sourceLongerval;
			private String exchangeTimezoneShortName;
			private String market;
			private Long regularMarketTime;
			private Long regularMarketVolume;
			private Long gmtOffSetMilliseconds;
			private String exchange;
			private Boolean tradeable;
			private String symbol;
			private String shortName;
			private String messageBoardId;
			private Long openLongerest;
			private String underlyingSymbol;
			private String underlyingExchangeSymbol;
			private Long expireDate;
			private Boolean contractSymbol;
			private String headSymbolAsString;
			private Double twoHundredDayAverage;
			private Double twoHundredDayAverageChangePercent;
			private Double fiftyTwoWeekHighChange;
			private Double fiftyDayAverage;
			private Double fiftyTwoWeekHighChangePercent;
			private Double fiftyTwoWeekLowChange;
			private Double fiftyDayAverageChange;
			private Double twoHundredDayAverageChange;
			private Double fiftyTwoWeekLow;
			private Long averageDailyVolume3Month;
			private Double fiftyDayAverageChangePercent;
			private Long averageDailyVolume10Day;
			private Double fiftyTwoWeekHigh;
			private String financialCurrency;
			private Double fiftyTwoWeekLowChangePercent;
			private Double priceToBook;
			private Double trailingPE;
			private Double bookValue;
			private Long earningsTimestampEnd;
			private Double epsTrailingTwelveMonths;
			private Long marketCap;
			private Long earningsTimestampStart;
			private Long sharesOutstanding;
			private String longName;
		}
	}
}