package fr.ymanvieu.trading.symbol.dto;

import static fr.ymanvieu.trading.TestUtils.symbol;
import static fr.ymanvieu.trading.assertions.ObjectAssertions.assertThat;

import org.junit.Test;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SymbolMapperTest {

	@Test
	public void testToDto() {
		SymbolEntity se = symbol("TOTO", "toto", "country", null);

		SymbolDTO result = SymbolMapper.MAPPER.toDto(se);

		assertThat(result).hasAllFieldsSet();

		log.info("result: {}", result);
	}
}