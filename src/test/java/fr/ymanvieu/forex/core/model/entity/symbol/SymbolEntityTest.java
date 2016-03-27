package fr.ymanvieu.forex.core.model.entity.symbol;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SymbolEntityTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@SuppressWarnings("unused")
	@Test
	public void testCurrencyEntityStringStringString() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("code size");

		new SymbolEntity("deddeededede");
	}
}