/**
 * Copyright (C) 2019 Yoann Manvieu
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
package fr.ymanvieu.trading.config;

import static fr.ymanvieu.trading.test.io.ClasspathFileReader.readFile;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@RunWith(SpringRunner.class)
@JsonTest
public class ActiveMQConfigTest {
	
	@Autowired
	private ObjectMapper mapper;

	@Test
	public void testSerialization() throws IOException {

		SymbolEntity from = new SymbolEntity("FROM", "from", "fcountry", null);
		SymbolEntity to = new SymbolEntity("TO", "to", "tcountry", null);

		RatesUpdatedEvent expected = new RatesUpdatedEvent()
				.setRates(asList(new LatestRate(from, to, new BigDecimal("25.5"), parse("2017-09-23T19:11:01+02:00"))));

		RatesUpdatedEvent result = mapper.readValue(readFile("/rate-update-event.json"), RatesUpdatedEvent.class);

		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
}