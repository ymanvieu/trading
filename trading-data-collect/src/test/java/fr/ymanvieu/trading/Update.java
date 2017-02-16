/**
 * Copyright (C) 2015 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

import com.google.common.base.Stopwatch;

import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateUpdaterService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

//@SpringBootApplication
public class Update {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RateUpdaterService hRateRepo;

	@Autowired
	private SymbolRepository symbolRepo;

	public static void main(String[] args) {
		SpringApplication.run(Update.class, args);
	}

	@Bean
	public ApplicationRunner run() {
		return (args) -> {

			List<SymbolEntity> symbols = symbolRepo.findAll();
			SymbolEntity euro = symbolRepo.findOne("EUR");

			ThreadLocalRandom r = ThreadLocalRandom.current();

			IntStream.range(1, 11).forEach(ii -> {

				List<Quote> rates = new ArrayList<>();

				int nb = ii * 1000;

				IntStream.range(0, nb).forEach(i -> {
					float fl = r.nextFloat() * symbols.size();

					Calendar c = Calendar.getInstance();
					c.setTime(new Date(r.nextLong(-30610227600000L, 253402210800000L)));

					rates.add(new Quote(euro.getCode(), symbols.get((int) fl).getCode(), new BigDecimal(fl), c.getTime()));
				});

				Stopwatch sw = Stopwatch.createStarted();
				hRateRepo.updateRates(rates);
				float rps = nb / (sw.elapsed(TimeUnit.MILLISECONDS) / 1000f);
				log.info("save {} rates in {} ({}/s)", nb, sw, rps);
			});
		};
	}
}