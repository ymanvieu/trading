package fr.ymanvieu.forex.core.service;

import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static fr.ymanvieu.forex.core.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.forex.core.ForexApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_data.sql")
public class RateServiceTest {

	@Autowired
	private RateService service;

	@Test
	public void testGetMin() throws Exception {
		Date result = service.getMin(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-01 22:42:10.0 CET"));
	}
	
	@Test
	public void testGetMinOK_NoElement() {
		assertThat(service.getMin(USD, "TOTO")).isNull();
	}

	@Test
	public void testGetMax() throws Exception {
		Date result = service.getMax(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}
	
	@Test
	public void testGetMaxOK_NoElement() {
		assertThat(service.getMax(USD, "TOTO")).isNull();
	}
}
