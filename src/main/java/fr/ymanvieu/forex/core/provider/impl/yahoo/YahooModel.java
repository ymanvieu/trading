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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonIgnoreProperties("meta")
@JsonRootName("list")
public class YahooModel {

	private List<YahooResource> resources;

	public List<YahooResource> getResources() {
		return resources;
	}

	public static class YahooResource {

		private YahooSubResource resource;

		public YahooSubResource getResource() {
			return resource;
		}
	}

	@JsonIgnoreProperties("classname")
	public static class YahooSubResource {

		private YahooFields fields;

		public YahooFields getFields() {
			return fields;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class YahooFields {

		private String symbol;

		private Date utctime;

		private BigDecimal price;

		@JsonProperty("issuer_name")
		private String issuerName;

		public String getSymbol() {
			return symbol;
		}

		public Date getUtctime() {
			return utctime;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public String getIssuerName() {
			return issuerName;
		}
	}
}
