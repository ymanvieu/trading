package fr.ymanvieu.trading.provider.dto;

import static fr.ymanvieu.trading.test.assertions.ObjectAssertions.assertThat;

import org.junit.Test;

import fr.ymanvieu.trading.provider.entity.PairEntity;

public class PairMapperTest {

	@Test
	public void testToPairDto() {
		PairEntity p = new PairEntity("USDEUR=X", "USD/EUR", "USD", "EUR", "YAHOO");

		PairDTO result = PairMapper.MAPPER.toPairDto(p);

		assertThat(result).hasAllFieldsSet();
	}

}
