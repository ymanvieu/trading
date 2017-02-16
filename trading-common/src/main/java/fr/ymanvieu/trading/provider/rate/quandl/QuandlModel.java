/**
 * Copyright (C) 2014 Yoann Manvieu
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
package fr.ymanvieu.trading.provider.rate.quandl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QuandlModel {

	private Dataset dataset;

	public Dataset getDataset() {
		return dataset;
	}

	public static class Dataset {
		private List<Rate> data;

		public List<Rate> getData() {
			return data;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonFormat(shape = Shape.ARRAY)
	public static class Rate {

		private Calendar date;

		private BigDecimal value;

		public Calendar getDate() {
			return date;
		}

		@JsonProperty(index = 0)
		public void setDate(Calendar date) {
			this.date = date;
		}

		public BigDecimal getValue() {
			return value;
		}

		@JsonProperty(index = 1)
		public void setValue(BigDecimal open) {
			this.value = open;
		}
	}
}
