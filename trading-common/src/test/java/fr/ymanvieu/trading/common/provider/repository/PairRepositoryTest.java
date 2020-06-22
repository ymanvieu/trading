/**
 * Copyright (C) 2020 Yoann Manvieu
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
package fr.ymanvieu.trading.common.provider.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import fr.ymanvieu.trading.test.time.DateParser;

@RunWith(SpringRunner.class)
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
			assertThat(up.getSource().getCode()).isEqualTo("RR");
			assertThat(up.getSource().getName()).isEqualTo("Rolls Royce");
			assertThat(up.getTarget().getCode()).isEqualTo("GBP");
			assertThat(up.getTarget().getName()).isEqualTo("British Pound Sterling");
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
				assertThat(up.getSource().getCode()).isEqualTo("RR");
				assertThat(up.getSource().getName()).isEqualTo("Rolls Royce");
				assertThat(up.getTarget().getCode()).isEqualTo("GBP");
				assertThat(up.getTarget().getName()).isEqualTo("British Pound Sterling");
				assertThat(up.getExchange()).isEqualTo("London");
				assertThat(up.getProviderCode()).isEqualTo("YAHOO");
			});
	}

	@Test
	public void testFindAllUpdatedPair() throws Exception {
		var updatedPairs = pairRepository.findAllUpdatedPair();
		
		assertThat(updatedPairs)
			.satisfies(up -> {
				assertThat(up.getSymbol()).isEqualTo("UBI.PA");
			}, atIndex(0))
			.satisfies(up -> {
				assertThat(up.getSymbol()).isEqualTo("GFT.PA");
			}, atIndex(1))
			.satisfies(up -> {
				assertThat(up.getLastUpdate()).isEqualTo(DateParser.parse("2020-03-12T15:10:00"));
				assertThat(up.getSymbol()).isEqualTo("RR.L");
				assertThat(up.getName()).isEqualTo("Rolls Royce Holdings plc");
				assertThat(up.getSource().getCode()).isEqualTo("RR");
				assertThat(up.getSource().getName()).isEqualTo("Rolls Royce");
				assertThat(up.getTarget().getCode()).isEqualTo("GBP");
				assertThat(up.getTarget().getName()).isEqualTo("British Pound Sterling");
				assertThat(up.getExchange()).isEqualTo("London");
				assertThat(up.getProviderCode()).isEqualTo("YAHOO");
			}, atIndex(2));
	}

}
