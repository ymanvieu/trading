package fr.ymanvieu.trading.webapp.config;

import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.test.context.ContextConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.webapp.rate.messaging.RatesStompHandler;

@JsonTest
@ContextConfiguration(classes = JmsConfig.class)
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
