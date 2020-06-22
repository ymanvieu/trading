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
package fr.ymanvieu.trading.webapp.portofolio.dto;

import static fr.ymanvieu.trading.test.assertions.ObjectAssertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import fr.ymanvieu.trading.common.portofolio.AssetInfo;
import fr.ymanvieu.trading.common.portofolio.Portofolio;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.webapp.config.MapperTestConfig;

@RunWith(SpringRunner.class)
@Import(MapperTestConfig.class)
public class PortofolioDTOMapperTest {
	
	@Autowired
	private PortofolioDTOMapper mapper;

	@Test
	public void testToAssetDto() {
		AssetInfo ai = new AssetInfo(new SymbolEntity("FROM", "from", "fcountry", null), new SymbolEntity("TO", "to", "tcountry", null));

		ai.setCurrentRate(4d);
		ai.setCurrentValue(1d);
		ai.setPercentChange(7.4d);
		ai.setQuantity(8.00d);
		ai.setValue(0.8d);
		ai.setValueChange(151d);

		AssetDTO res = mapper.toAssetDto(ai);

		assertThat(res).hasAllFieldsSet();
	}

	@Test
	public void testToPortofolioDto() {
		AssetInfo baseCurrency = new AssetInfo(new SymbolEntity("EUR", "euro", "eu", null), null);
		AssetInfo ai = new AssetInfo(new SymbolEntity("FROM", "from", "fcountry", null), new SymbolEntity("TO", "to", "tcountry", null));

		Portofolio p = new Portofolio(baseCurrency, Arrays.asList(ai), 1500f, 10.94f, 145f);

		PortofolioDTO result = mapper.toPortofolioDto(p);
		
		assertThat(result).hasAllFieldsSet();
	}

}
