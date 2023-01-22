package fr.ymanvieu.trading.webapp.portofolio.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.common.portofolio.AssetInfo;
import fr.ymanvieu.trading.common.portofolio.Portofolio;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.test.config.MapperTestConfig;

@ExtendWith(SpringExtension.class)
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

		assertThat(res).hasNoNullFieldsOrProperties();
	}

	@Test
	public void testToPortofolioDto() {
		AssetInfo baseCurrency = new AssetInfo(new SymbolEntity("EUR", "euro", "eu", null), null);
		AssetInfo ai = new AssetInfo(new SymbolEntity("FROM", "from", "fcountry", null), new SymbolEntity("TO", "to", "tcountry", null));

		Portofolio p = new Portofolio(baseCurrency, List.of(ai), 1500f, 10.94f, 145f);

		PortofolioDTO result = mapper.toPortofolioDto(p);
		
		assertThat(result).hasNoNullFieldsOrProperties();
	}

}
