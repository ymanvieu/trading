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
package fr.ymanvieu.forex.core.provider.impl;

import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.provider.AProvider;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooModel;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooModel.YahooFields;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooModel.YahooResource;
import fr.ymanvieu.forex.core.util.StringUtils;

/**
 * Supporting 172 currencies including : <br>
 * <ul>
 * <li>Copper (XCP)</li>
 * <li>Zambian kwacha (ZMW)</li>
 * </ul>
 */
@Component
public class Yahoo extends AProvider {

	private static final Logger LOG = LoggerFactory.getLogger(Yahoo.class);

	@Value("${yahoo.url.latest.currencies}")
	private String urlCurrencies;

	private final ObjectMapper mapper = new ObjectMapper();

	public Yahoo() {
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
	}

	@Override
	public List<RateEntity> getRates() throws IOException {
		String response = sendGet(urlCurrencies);

		try {
			response = clean(response);

			YahooModel model = mapper.readValue(response, YahooModel.class);

			List<RateEntity> rates = new ArrayList<>();

			for (YahooResource o : model.getResources()) {
				YahooFields yf = o.getResource().getFields();
				String symbol = yf.getSymbol().substring(0, 3);

				if (USD.equals(symbol)) {
					continue;
				}

				rates.add(new RateEntity(USD, symbol, yf.getPrice(), yf.getUtctime()));
			}

			return rates;
		} catch (Exception e) {
			LOG.error("Response: {}", StringUtils.toOneLine(response));
			throw e;
		}
	}

	private String clean(String response) {

		String resp = response.trim();
		int startIndex = resp.indexOf("{");

		if (startIndex > 0) {
			LOG.warn("Response has to be cleaned [start]");
			resp = resp.substring(startIndex);
		}

		int endIndex = resp.lastIndexOf("}");

		if (endIndex < resp.length() - 1) {
			LOG.warn("Response has to be cleaned [end]");
			resp = resp.substring(0, endIndex + 1);
		}

		return resp;
	}
}
