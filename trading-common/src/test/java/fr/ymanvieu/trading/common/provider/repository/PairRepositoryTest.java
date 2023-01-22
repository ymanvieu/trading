package fr.ymanvieu.trading.common.provider.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.test.time.DateParser;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/insert_data.sql")
public class PairRepositoryTest {
	
	@Autowired
	private PairRepository pairRepository;

	@Test
	public void testFindAllUpdatedPairBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase_symbol() throws Exception {
		var symbol = "r.";
		
		var updatedPairs = pairRepository.findAllUpdatedPairBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(symbol, null);
		
		assertThat(updatedPairs).hasSize(1)
		.element(0).satisfies(up -> {
			assertThat(up.getLastUpdate()).isEqualTo(DateParser.parse("2020-03-12T15:10:00"));
			assertThat(up.getSymbol()).isEqualTo("RR.L");
			assertThat(up.getName()).isEqualTo("Rolls Royce Holdings plc");
			assertThat(up.getExchange()).isEqualTo("London");
			assertThat(up.getProviderCode()).isEqualTo("YAHOO");
		});
	}
	
	@Test
	public void testFindAllUpdatedPairBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase_name() throws Exception {
		var name = "roLls";
		
		var updatedPairs = pairRepository.findAllUpdatedPairBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(null, name);
		
		assertThat(updatedPairs).hasSize(1)
			.element(0).satisfies(up -> {
				assertThat(up.getLastUpdate()).isEqualTo(DateParser.parse("2020-03-12T15:10:00"));
				assertThat(up.getSymbol()).isEqualTo("RR.L");
				assertThat(up.getName()).isEqualTo("Rolls Royce Holdings plc");
				assertThat(up.getExchange()).isEqualTo("London");
				assertThat(up.getProviderCode()).isEqualTo("YAHOO");
			});
	}

	@Test
	public void testFindAllUpdatedPair() throws Exception {
		var updatedPairs = pairRepository.findAllUpdatedPair();
		
		assertThat(updatedPairs)
			.satisfies(up -> {
				assertThat(up.getLastUpdate()).isEqualTo(DateParser.parse("2020-03-12T15:10:00"));
				assertThat(up.getSymbol()).isEqualTo("RR.L");
				assertThat(up.getName()).isEqualTo("Rolls Royce Holdings plc");
				assertThat(up.getExchange()).isEqualTo("London");
				assertThat(up.getProviderCode()).isEqualTo("YAHOO");
			}, atIndex(0))
			.satisfies(up -> {
				assertThat(up.getSymbol()).isEqualTo("GFT.PA");
			}, atIndex(1))
			.satisfies(up -> {
				assertThat(up.getSymbol()).isEqualTo("UBI.PA");
			}, atIndex(2));
	}

}
