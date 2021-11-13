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

import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.webapp.rate.messaging.RatesStompHandler;

@ExtendWith(SpringExtension.class)
@Import(JmsConfig.class)
@TestPropertySource(properties = "spring.activemq.broker-url=test")
@JsonTest
@MockBean({DefaultJmsListenerContainerFactory.class})
public class JmsConfigTest {
	
	@Autowired
	private JmsConfig jmsConfig;

	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private RatesStompHandler ratesStompHandler;

	@Value("classpath:rate-update-event.json")
	private Resource rateUpdateEvent;

	@Test
	public void testSerialization() throws Exception {

		Symbol to = new Symbol("TO", "to", "tcountry", null);
		Symbol from = new Symbol("FROM", "from", null, to);

		RatesUpdatedEvent expected = new RatesUpdatedEvent()
				.setRates(List.of(new Rate(from, to, new BigDecimal("25.5"), parse("2017-09-23T19:11:01+02:00"))));

		RatesUpdatedEvent result = mapper.readValue(rateUpdateEvent.getFile(), RatesUpdatedEvent.class);

		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	public void testReceiveMessage() {
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