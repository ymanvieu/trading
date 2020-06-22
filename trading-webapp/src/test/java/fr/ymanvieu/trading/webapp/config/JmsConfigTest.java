/**
 * Copyright (C) 2017 Yoann Manvieu
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
package fr.ymanvieu.trading.webapp.config;

import static fr.ymanvieu.trading.test.io.ClasspathFileReader.readFile;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.webapp.rate.messaging.RatesStompHandler;

@RunWith(SpringRunner.class)
@Import(JmsConfig.class)
@JsonTest
public class JmsConfigTest {
	
	@Autowired
	private JmsConfig jmsConfig;
	
	@MockBean
	private DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private RatesStompHandler ratesStompHandler;

	@Test
	public void testSerialization() throws IOException {

		Symbol from = new Symbol("FROM", "from", "fcountry", null);
		Symbol to = new Symbol("TO", "to", "tcountry", null);

		RatesUpdatedEvent expected = new RatesUpdatedEvent()
				.setRates(asList(new Rate(from, to, new BigDecimal("25.5"), parse("2017-09-23T19:11:01+02:00"))));

		RatesUpdatedEvent result = mapper.readValue(readFile("/rate-update-event.json"), RatesUpdatedEvent.class);

		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testReceiveMessage() throws Exception {
		// GIVEN
		Symbol from = new Symbol("FROM", "from", "fcountry", null);
		Symbol to = new Symbol("TO", "to", "tcountry", null);

		RatesUpdatedEvent event = new RatesUpdatedEvent()
				.setRates(List.of(new Rate(from, to, new BigDecimal("25.5"), parse("2017-09-23T19:11:01+02:00"))));

		
		// WHEN
		jmsConfig.receiveMessage(event);
		
		// THEN
		verify(ratesStompHandler).send(any());
	}
}