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

		ai.setCurrentRate(4f);
		ai.setCurrentValue(1f);
		ai.setPercentChange(7.4f);
		ai.setQuantity(8.00f);
		ai.setValue(0.8f);
		ai.setValueChange(151f);

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
