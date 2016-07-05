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
package fr.ymanvieu.trading;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.core.io.ClassPathResource;

import com.google.common.io.Files;

import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

public class TestUtils {

	public static String readFile(String path) throws IOException {
		return Files.toString(new ClassPathResource(path).getFile(), StandardCharsets.UTF_8);
	}

	public static RateEntity rate(String from, String to, BigDecimal rate, Date date) {
		return new RateEntity(from, to, rate, date);
	}

	public static Quote quote(String code, String currency, BigDecimal rate, Date date) {
		return new Quote(code, currency, rate, date);
	}

	public static SymbolEntity symbol(String code, String name, String countryFlag, SymbolEntity currency) {
		SymbolEntity se = new SymbolEntity(code);
		se.setName(name);
		se.setCountryFlag(countryFlag);
		se.setCurrency(currency);
		return se;
	}
}