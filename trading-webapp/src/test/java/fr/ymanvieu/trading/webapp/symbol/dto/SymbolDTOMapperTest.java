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
package fr.ymanvieu.trading.webapp.symbol.dto;

import static fr.ymanvieu.trading.test.assertions.ObjectAssertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.webapp.config.MapperTestConfig;

@ExtendWith(SpringExtension.class)
@Import(MapperTestConfig.class)
public class SymbolDTOMapperTest {
	
	@Autowired
	private SymbolDTOMapper mapper;

	@Test
	public void testToDto() {
		Symbol se = new Symbol("TOTO", "toto", "country", null);

		SymbolDTO result = mapper.toDto(se);

		assertThat(result).hasAllFieldsSet();
	}
}