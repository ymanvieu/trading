package fr.ymanvieu.trading.common.symbol;

import static fr.ymanvieu.trading.common.symbol.Currency.EUR;
import static fr.ymanvieu.trading.common.symbol.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;

@SpringBootTest
@Transactional
public class SymbolServiceTest {

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private SymbolService symbolService;

	@Test
	public void createSymbol_notExist() {
		// given
		String code = USD;
		String name = "US Dollar";
		String countryFlag = "us";

		// when
		Symbol result = symbolService.createSymbol(code, name, countryFlag, null);

		// then
		assertThat(result).usingRecursiveComparison().isEqualTo(new SymbolEntity(code, name, countryFlag, null));
		assertThat(result).usingRecursiveComparison().isEqualTo(symbolRepo.findById(code).get());
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void createSymbol_NotExistWithExistingCurrency() {
		// given
		String code = "TOTO";
		String name = "toto";
		var flag = "flag";
		String currencyCode = USD;

		// when
		Symbol result = symbolService.createSymbol(code, name, flag, currencyCode);

		// then
		assertThat(result).usingRecursiveComparison().isEqualTo(new Symbol(code, name, flag, new Symbol(USD, "US Dollar", "us", null)));
		
		var savedSymbol = symbolRepo.findById(code).get();
		assertThat(savedSymbol.getCode()).isEqualTo(code);
		assertThat(savedSymbol.getName()).isEqualTo(name);
		assertThat(savedSymbol.getCountryFlag()).isEqualTo(flag);
		assertThat(savedSymbol.getCurrency().getCode()).isEqualTo(currencyCode);
	}

	@Test
	public void createSymbol_NotExistWithNotExistingCurrency() {
		// given
		String code = "TOTO";
		String name = "toto";
		String currencyCode = "XXX";

		// when
		assertThatThrownBy(() -> symbolService.createSymbol(code, name, null, currencyCode))
				.isInstanceOf(SymbolException.class)
				.hasMessageContaining("symbol.error.unknown: [XXX]");
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void createSymbol_AlreadyExist() {
		// given
		String name = "US Dollar";
		String countryFlag = "us";

		// when
		assertThatThrownBy(() -> symbolService.createSymbol(USD, name, countryFlag, null))
				.isInstanceOf(SymbolException.class)
				.hasMessage("symbol.error.already_exists: [USD]");

	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void getForCode() {
		assertThat(symbolService.getForCode(USD))
		.hasValueSatisfying((result) -> {
			assertThat(result).isEqualTo(new Symbol(USD, "US Dollar", "us", null));
			assertThat(result).usingRecursiveComparison().ignoringFields( "currencyCode").isEqualTo(symbolRepo.findById(USD).get());
		});
	}

	@Test
	public void getForCode_NotExist() {
		assertThat(symbolService.getForCode(USD)).isNotPresent();
	}

	@Sql("/sql/insert_user_symbol_favorite.sql")
	@Test
	public void createFavoriteSymbol() {
		symbolService.createFavoriteSymbol(USD, EUR, 2);
	}

	@Sql("/sql/insert_user_symbol_favorite.sql")
	@Test
	public void testDeleteFavoriteSymbol() {
		symbolService.deleteFavoriteSymbol("UBI", EUR, 2);
	}
}
