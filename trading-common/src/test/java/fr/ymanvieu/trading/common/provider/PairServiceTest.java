package fr.ymanvieu.trading.common.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.common.config.MapperTestConfig;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({ PairService.class, MapperTestConfig.class })
@Sql("/sql/insert_data.sql")
public class PairServiceTest {

	private static final String YAHOO = "YAHOO";

	@Autowired
	private PairService pairService;

	@Test
	public void testGetForCodeAndProvider() {
		Pair result = pairService.getForCodeAndProvider("UBI.PA", YAHOO);

		assertThat(result.getSymbol()).isEqualTo("UBI.PA");
		assertThat(result.getName()).isEqualTo("Ubisoft Entertainment SA");
		assertThat(result.getSource().getCode()).isEqualTo("UBI");
		assertThat(result.getTarget().getCode()).isEqualTo("EUR");
		assertThat(result.getProviderCode()).isEqualTo(YAHOO);
	}

	@Test
	public void testCreate() {
		String code = "XAUUSD=X";
		String name = "XAU/USD";
		String source = "XAU";
		String target = "USD";

		pairService.create(code, name, source, target, null, YAHOO);

		Pair result = pairService.getForCodeAndProvider(code, YAHOO);

		assertThat(result).extracting("symbol", "name", "source.code", "target.code", "providerCode") //
				.containsExactly(code, name, source, target, YAHOO);
	}

	@Test
	public void testRemove() {
		String code = "UBI.PA";

		pairService.remove(0);

		assertThat(pairService.getForCodeAndProvider(code, YAHOO)).isNull();
	}
	
	@Test
	public void testRemove_notFound() {
		// when
		assertThatThrownBy(() -> pairService.remove(1))
				.isInstanceOf(PairException.class)
				.hasMessage("pair.error.not-found: [1]");
	}

	@Test
	public void testGetAllFromProvider() {
		List<Pair> result = pairService.getAllFromProvider(YAHOO);

		assertThat(result).extracting("symbol").containsExactlyInAnyOrder("UBI.PA", "GFT.PA", "RR.L");
	}

	@Test
	public void testGetAllWithSymbolOrNameContaining_Code() {
		List<UpdatedPair> result = pairService.getAllWithSymbolOrNameContaining("ub");

		assertThat(result).extracting("symbol").containsExactly("UBI.PA");
	}

	@Test
	public void testGetAllWithSymbolOrNameContaining_Name() {
		List<UpdatedPair> result = pairService.getAllWithSymbolOrNameContaining("enT");

		assertThat(result).extracting("symbol").containsExactly("UBI.PA");
	}
}
