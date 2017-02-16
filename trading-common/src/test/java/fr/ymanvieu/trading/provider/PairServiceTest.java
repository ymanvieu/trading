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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import fr.ymanvieu.trading.provider.entity.PairEntity;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_data.sql")
public class PairServiceTest {

	@Autowired
	private PairService pairService;

	@Test
	public void testGetForCode() {
		PairEntity result = pairService.getForCode("UBI.PA");

		assertThat(result.getSymbol()).isEqualTo("UBI.PA");
		assertThat(result.getName()).isEqualTo("Ubisoft Entertainment SA");
		assertThat(result.getSource().getCode()).isEqualTo("UBI");
		assertThat(result.getTarget().getCode()).isEqualTo("EUR");
		assertThat(result.getProviderCode()).isEqualTo("YAHOO");
	}

	@Test
	public void testCreate() {
		String code = "XAUUSD=X";
		String name = "XAU/USD";
		String source = "XAU";
		String target = "USD";
		String providerCode = "YAHOO";

		PairEntity returnedResult = pairService.create(code, name, source, target, providerCode);

		PairEntity result = pairService.getForCode(code);

		assertThat(returnedResult).isEqualToComparingFieldByFieldRecursively(result);

		assertThat(result).extracting("symbol", "name", "source.code", "target.code", "providerCode") //
				.containsExactly(code, name, source, target, providerCode);
	}

	@Test
	public void testRemove() {
		String code = "UBI.PA";

		assertThat(pairService.getForCode(code)).isNotNull();

		pairService.remove(code);

		assertThat(pairService.getForCode(code)).isNull();
	}

	@Test
	public void testGetAll() {
		List<PairEntity> result = pairService.getAll();

		assertThat(result).extracting("symbol").containsExactlyInAnyOrder("UBI.PA", "GFT.PA", "RR.L");
	}

	@Test
	public void testGetAllWithSymbolOrNameContaining_Code() {
		List<PairEntity> result = pairService.getAllWithSymbolOrNameContaining("ub");

		assertThat(result).extracting("symbol").containsExactly("UBI.PA");
	}

	@Test
	public void testGetAllWithSymbolOrNameContaining_Name() {
		List<PairEntity> result = pairService.getAllWithSymbolOrNameContaining("enT");

		assertThat(result).extracting("symbol").containsExactly("UBI.PA");
	}
}