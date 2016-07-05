/**
 * Copyright (C) 2016 Yoann Manvieu
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
package fr.ymanvieu.trading.provider.lookup.yahoo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("ResultSet")
public class YahooLookupModel {

	@JsonProperty("Query")
	private String query;

	@JsonProperty("Result")
	private List<YahooCompanyLookupResult> result;

	public String getQuery() {
		return query;
	}

	public List<YahooCompanyLookupResult> getResult() {
		return result;
	}

	public static class YahooCompanyLookupResult {

		private String symbol;
		private String name;
		private String exch;
		private String type;
		private String exchDisp;
		private String typeDisp;

		public String getSymbol() {
			return symbol;
		}

		public String getName() {
			return name;
		}
		
		protected void setName(String name) {
			this.name = name;
		}

		public String getExch() {
			return exch;
		}

		public String getType() {
			return type;
		}

		public String getExchDisp() {
			return exchDisp;
		}

		public String getTypeDisp() {
			return typeDisp;
		}
	}
}
