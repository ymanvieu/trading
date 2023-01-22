package fr.ymanvieu.trading.webapp.symbol.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.test.config.MapperTestConfig;

@ExtendWith(SpringExtension.class)
@Import(MapperTestConfig.class)
public class SymbolDTOMapperTest {
	
	@Autowired
	private SymbolDTOMapper mapper;

	@Test
	public void testToDto() {
		Symbol se = new Symbol("TOTO", "toto", "country", null);

		SymbolDTO result = mapper.toDto(se);

		assertThat(result).hasNoNullFieldsOrProperties();
	}
}
