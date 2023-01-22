package fr.ymanvieu.trading.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

	@Test
	public void testFormat() {
		assertThat(StringUtils.format("I''m {0}", "Batman")).isEqualTo("I'm Batman");
	}
}
