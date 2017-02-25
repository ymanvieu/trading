package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.rate.AverageRangeType.DAY;
import static fr.ymanvieu.trading.rate.AverageRangeType.HOUR;
import static fr.ymanvieu.trading.rate.AverageRangeType.NONE;
import static fr.ymanvieu.trading.rate.AverageRangeType.WEEK;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class AverageRangeTypeTest {

	protected static Object[][] parametersForTestGetRange() {
		return new Object[][] {
				{ null, null, WEEK },
				{ "2013-08-01T00:05:00+02:00", "2015-08-01T00:05:00+02:00", WEEK },
				{ "2015-02-01T00:05:00+01:00", "2015-08-01T00:05:00+02:00", DAY },
				{ "2015-02-01T00:05:00+01:00", "2015-08-01T00:03:00+02:00", HOUR },
				{ "2015-02-10T00:05:00+01:00", "2015-02-17T00:05:00+01:00", HOUR },
				{ "2015-02-10T00:05:00+01:00", "2015-02-17T00:04:00+01:00", NONE },
		};
	}

	@Test
	@Parameters
	public void testGetRange(@DateParam Date start, @DateParam Date end, AverageRangeType expected) {
		assertThat(AverageRangeType.getRange(start, end)).isEqualTo(expected);
	}
}
