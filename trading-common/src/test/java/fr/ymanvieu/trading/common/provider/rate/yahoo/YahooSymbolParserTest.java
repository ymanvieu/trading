package fr.ymanvieu.trading.common.provider.rate.yahoo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class YahooSymbolParserTest {

    private static Object[][] testParseSource() {
        return new Object[][] {
            { "BTCUSD=X", "BTC" },
            { "XAU=X", "USD" },
            { "EDF.PA", "EDF" },
            { "MSFT", "MSFT" },
            { "TS_B.TO", "TS_B" },
            { "CL=F", "CL=F" },
            { "DOGE-USD", "DOGE" },
            { "THQN-B.ST", "THQN-B" },
            { "RDS-A", "RDS-A" },
            { "005930.KS", "005930" },
        };
    }

    @ParameterizedTest
    @MethodSource
    public void testParseSource(String code, String expectedResult) {
        assertThat(YahooSymbolParser.parseSource(code)).isEqualTo(expectedResult);
    }

    private static Object[][] testParseTarget() {
        return new Object[][] {
            { "BTCUSD=X", "USD" },
            { "XAU=X", "XAU" },
            { "EDF.PA", null },
            { "MSFT", null },
            { "TS_B.TO", null },
            { "CL=F", null },
            { "DOGE-USD", "USD" },
            { "THQN-B.ST", null },
            { "RDS-A", null },
            { "005930.KS", null },
        };
    }

    @ParameterizedTest
    @MethodSource
    public void testParseTarget(String code, String expectedResult) {
        assertThat(YahooSymbolParser.parseTarget(code)).isEqualTo(expectedResult);
    }
}
