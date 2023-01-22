package fr.ymanvieu.trading.datacollect.config;

import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.core.io.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.common.symbol.Symbol;

@JsonTest
public class JmsBrokerConfigTest {
	
	@Autowired
	private ObjectMapper mapper;

	@Value("classpath:rate-update-event.json")
	private Resource rateUpdateEvent;

	@Test
	public void testSerialization() throws Exception {

		Symbol from = new Symbol("FROM", "from", "fcountry", null);
		Symbol to = new Symbol("TO", "to", "tcountry", null);

		RatesUpdatedEvent expected = new RatesUpdatedEvent()
				.setRates(List.of(new Rate(from, to, new BigDecimal("25.5"), parse("2017-09-23T19:11:01+02:00"))));

		RatesUpdatedEvent result = mapper.readValue(rateUpdateEvent.getFile(), RatesUpdatedEvent.class);

		assertThat(result).usingRecursiveComparison().isEqualTo(expected);
	}
}
