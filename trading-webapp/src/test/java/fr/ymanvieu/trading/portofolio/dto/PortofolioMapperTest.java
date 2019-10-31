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
package fr.ymanvieu.trading.portofolio.dto;

import static fr.ymanvieu.trading.test.assertions.ObjectAssertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

import fr.ymanvieu.trading.portofolio.AssetInfo;
import fr.ymanvieu.trading.portofolio.Portofolio;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

public class PortofolioMapperTest {

	@Test
	public void testToAssetDto() {
		AssetInfo ai = new AssetInfo(new SymbolEntity("FROM", "from", "fcountry", null), new SymbolEntity("TO", "to", "tcountry", null));

		ai.setCurrentRate(4d);
		ai.setCurrentValue(1d);
		ai.setPercentChange(7.4d);
		ai.setQuantity(8.00d);
		ai.setValue(0.8d);
		ai.setValueChange(151d);

		AssetDTO res = PortofolioMapper.MAPPER.toAssetDto(ai);

		assertThat(res).hasAllFieldsSet();
	}

	@Test
	public void testToPortofolioDto() {
		AssetInfo baseCurrency = new AssetInfo(new SymbolEntity("EUR", "euro", "eu", null), null);
		AssetInfo ai = new AssetInfo(new SymbolEntity("FROM", "from", "fcountry", null), new SymbolEntity("TO", "to", "tcountry", null));

		Portofolio p = new Portofolio(baseCurrency, Arrays.asList(ai), 1500f, 10.94f, 145f);

		PortofolioDTO result = PortofolioMapper.MAPPER.toPortofolioDto(p);
		
		assertThat(result).hasAllFieldsSet();
	}

}
