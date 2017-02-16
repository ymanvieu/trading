package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.rate.AverageRangeType.DAY;
import static fr.ymanvieu.trading.rate.AverageRangeType.HOUR;
import static fr.ymanvieu.trading.rate.AverageRangeType.NONE;
import static fr.ymanvieu.trading.rate.AverageRangeType.WEEK;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(DataProviderRunner.class)
public class AverageRangeTypeTest {

	@DataProvider
	public static Object[][] dataGetRange() throws ParseException {
		return new Object[][] {
				{ null, null, WEEK },
				{ parse("2013-08-01 00:05:00.0 CEST"), parse("2015-08-01 00:05:00.0 CEST"), WEEK },
				{ parse("2015-02-01 00:05:00.0 CET"), parse("2015-08-01 00:05:00.0 CEST"), DAY },
				{ parse("2015-02-01 00:05:00.0 CET"), parse("2015-08-01 00:03:00.0 CEST"), HOUR },
				{ parse("2015-02-10 00:05:00.0 CET"), parse("2015-02-17 00:05:00.0 CET"), HOUR },
				{ parse("2015-02-10 00:05:00.0 CET"), parse("2015-02-17 00:04:00.0 CET"), NONE },
		};
	}

	@Test
	@UseDataProvider("dataGetRange")
	public void testGetRange(Date start, Date end, AverageRangeType expected) {
		assertThat(AverageRangeType.getRange(start, end)).isEqualTo(expected);
	}
}
