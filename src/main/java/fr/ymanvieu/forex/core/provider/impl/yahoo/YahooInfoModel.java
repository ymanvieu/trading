/**
 * Copyright (C) 2015 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.forex.core.provider.impl.yahoo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties({ "created", "lang", "count" })
@JsonRootName("query")
public class YahooInfoModel {

	private YahooResult results;

	public YahooResult getResults() {
		return results;
	}

	public static class YahooResult {

		private YahooQuote quote;

		public YahooQuote getQuote() {
			return quote;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class YahooQuote {

		private String symbol;

		@JsonProperty("Currency")
		private String currency;

		public String getCurrency() {
			return currency;
		}

		public String getSymbol() {
			return symbol;
		}
	}
}
