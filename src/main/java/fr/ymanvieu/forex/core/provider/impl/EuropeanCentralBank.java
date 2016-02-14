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
import static fr.ymanvieu.forex.core.util.MathUtils.invert;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.provider.AProvider;
import fr.ymanvieu.forex.core.provider.impl.ecb.Day;
import fr.ymanvieu.forex.core.provider.impl.ecb.Rate;
import fr.ymanvieu.forex.core.provider.impl.ecb.Result;
import fr.ymanvieu.forex.core.util.CurrencyUtils;

/**
 * European Central Bank daily update.
 * <p>
 * Update occurs at 15:00 Europe/Paris according to the <a href=
 * "http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html" >ECB
 * website</a>.
 * 
 * @author ymanvieu
 * 
 */
@Component
public class EuropeanCentralBank extends AProvider {

	@Value("${ecb.url.history}")
	public String URL_HIST;

	private static final TimeZone TZ = TimeZone.getTimeZone("Europe/Paris");
	private static final String BASE_CURRENCY = CurrencyUtils.EUR;

	private final static String DATE_FORMAT_STRNG = "yyyy-MM-dd";

	@Override
	public List<RateEntity> getRates() throws IOException {
		return getHistoricalRates();
	}

	/**
	 * Returns rates with US dollar as base currency (for consistency with other
	 * data providers)
	 */
	@Override
	public List<RateEntity> getHistoricalRates() throws IOException {
		return getRates(sendGet(URL_HIST), true);
	}

	/**
	 * Returns rates with Euro as base currency (default)
	 */
	public List<RateEntity> getDefaultRates() throws IOException {
		return getRates(sendGet(URL_HIST), false);
	}

	private List<RateEntity> getRates(String response, boolean usdBase) throws IOException {
		SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT_STRNG);
		Calendar sfCalendar = sf.getCalendar();
		sfCalendar.setTimeZone(TZ);

		List<RateEntity> rates = new ArrayList<>();

		String cleanResponse = response.substring(response.indexOf("<Cube>"), response.lastIndexOf("</Cube>") + 7);
		try {
			JAXBContext context = JAXBContext.newInstance(Result.class);
			Unmarshaller un = context.createUnmarshaller();
			Result res = (Result) un.unmarshal(new StringReader(cleanResponse));

			for (Day d : res.getDays()) {
				Date date = d.getDate();
				sf.parse(sf.format(date));
				sfCalendar.set(Calendar.HOUR, 15);
				date = sf.getCalendar().getTime();

				if (!usdBase) {
					for (Rate r : d.getRates()) {
						rates.add(new RateEntity(BASE_CURRENCY, r.getTocur(), r.getValue(), date));
					}
				} else {
					BigDecimal usdToEurValue = null;

					for (Rate r : d.getRates()) {
						if (USD.equals(r.getTocur())) {
							usdToEurValue = invert(r.getValue());
							break;
						}
					}

					for (Rate r : d.getRates()) {
						if (USD.equals(r.getTocur())) {
							rates.add(new RateEntity(USD, BASE_CURRENCY, usdToEurValue, date));
						} else {
							rates.add(new RateEntity(USD, r.getTocur(),
									r.getValue().multiply(usdToEurValue), date));
						}
					}
				}
			}

			return rates;
		} catch (ParseException | JAXBException e) {
			throw new IOException(e);
		}
	}
}
