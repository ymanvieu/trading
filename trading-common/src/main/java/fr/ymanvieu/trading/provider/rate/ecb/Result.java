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
package fr.ymanvieu.trading.provider.rate.ecb;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Envelope")
@JsonIgnoreProperties({ "subject", "Sender" })
public class Result {

	@JacksonXmlProperty(localName = "Cube")
	private List<Node> days;

	public List<Node> getDays() {
		return days;
	}

	public static class Node {

		@JacksonXmlProperty(isAttribute = true)
		private String currency;

		@JacksonXmlProperty(isAttribute = true)
		private BigDecimal rate;

		@JacksonXmlProperty(isAttribute = true)
		private Calendar time;

		@JacksonXmlProperty(localName = "Cube")
		@JacksonXmlElementWrapper(useWrapping = false)
		private List<Node> rates;

		public String getCurrency() {
			return currency;
		}

		public BigDecimal getRate() {
			return rate;
		}

		public Calendar getTime() {
			return time;
		}

		public List<Node> getRates() {
			return rates;
		}
	}
}
