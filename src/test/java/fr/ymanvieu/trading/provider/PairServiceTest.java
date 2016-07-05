/**
 * Copyright (C) 2016 Yoann Manvieu
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
package fr.ymanvieu.trading.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.trading.TradingApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PairServiceTest {

	@Autowired
	private PairService pairService;

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetForCode() {
		Pair result = pairService.getForCode("UBI.PA");

		assertThat(result.getSymbol()).isEqualTo("UBI.PA");
		assertThat(result.getName()).isEqualTo("Ubisoft Entertainment SA");
		assertThat(result.getSource().getCode()).isEqualTo("UBI");
		assertThat(result.getTarget().getCode()).isEqualTo("EUR");
		assertThat(result.getProvider()).isEqualTo("YAHOO");
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testCreate() {
		String code = "XAUUSD=X";
		String name = "XAU/USD";
		String source = "XAU";
		String target = "USD";
		String provider = "YAHOO";

		Pair returnedResult = pairService.create(code, name, source, target, provider);

		Pair result = pairService.getForCode(code);

		assertThat(returnedResult).isEqualToComparingFieldByFieldRecursively(result);

		assertThat(result).extracting("symbol", "name", "source.code", "target.code", "provider") //
				.containsExactly(code, name, source, target, provider);
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testRemove() {
		String code = "UBI.PA";

		assertThat(pairService.getForCode(code)).isNotNull();

		pairService.remove(code);

		assertThat(pairService.getForCode(code)).isNull();
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetAll() {
		List<Pair> result = pairService.getAll();

		assertThat(result).extracting("symbol").containsExactlyInAnyOrder( //
				"UBI.PA", "GFT.PA", "RR.L");
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetAllWithSymbolOrNameContaining_Code() {
		List<Pair> result = pairService.getAllWithSymbolOrNameContaining("ub");

		assertThat(result).extracting("symbol").containsExactly("UBI.PA");
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetAllWithSymbolOrNameContaining_Name() {
		List<Pair> result = pairService.getAllWithSymbolOrNameContaining("enT");

		assertThat(result).extracting("symbol").containsExactly("UBI.PA");
	}
}