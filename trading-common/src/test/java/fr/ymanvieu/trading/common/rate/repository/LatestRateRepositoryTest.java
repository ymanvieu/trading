package fr.ymanvieu.trading.common.rate.repository;

import static fr.ymanvieu.trading.common.symbol.Currency.EUR;
import static fr.ymanvieu.trading.common.symbol.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import fr.ymanvieu.trading.common.rate.FavoriteRate;

@DataJpaTest
public class LatestRateRepositoryTest {

	@Autowired
	private LatestRateRepository repo;

	@Sql("/sql/insert_data.sql")
	@Sql("/sql/insert_eur_gbp.sql")
	@Test
	public void deleteByFromcurCodeAndTocurCode() {
		// when
		int result = repo.deleteByFromcurCodeAndTocurCode(USD, EUR);

		// then
		assertThat(result).isEqualTo(1);
		assertThat(repo.count()).isEqualTo(5);
	}

	@Sql("/sql/insert_data.sql")
	@Sql("/sql/insert_favorite_symbol.sql")
	@Test
	public void findAllWithFavorites() {
		
		List<FavoriteRate> result = repo.findAllWithFavorites(2);
		assertThat(result).hasSize(5)
		.satisfies(fr -> {
			assertThat(fr.getFromcur().getCode()).isEqualTo("BRE");
			assertThat(fr.getTocur().getCode()).isEqualTo("USD");
			assertThat(fr.getFavorite()).isTrue();
		}, atIndex(3));
	}
}
