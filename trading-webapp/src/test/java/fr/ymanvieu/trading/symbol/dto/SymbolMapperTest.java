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
package fr.ymanvieu.trading.symbol.dto;

import static fr.ymanvieu.trading.test.assertions.ObjectAssertions.assertThat;

import org.junit.Test;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SymbolMapperTest {

	@Test
	public void testToDto() {
		SymbolEntity se = new SymbolEntity("TOTO", "toto", "country", null);

		SymbolDTO result = SymbolMapper.MAPPER.toDto(se);

		assertThat(result).hasAllFieldsSet();

		log.info("result: {}", result);
	}
}