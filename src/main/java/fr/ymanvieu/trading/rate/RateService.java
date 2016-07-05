/**
 * Copyright (C) 2016 Yoann Manvieu
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
package fr.ymanvieu.trading.rate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.eventbus.EventBus;

import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.rate.entity.HistoricalRate;
import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.symbol.CurrencyInfo;
import fr.ymanvieu.trading.symbol.SymbolService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;
import fr.ymanvieu.trading.util.MathUtils;

@Service
public class RateService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String BASE_CURRENCY = CurrencyUtils.USD;

	private static Sort SORT_ASC_DATE = new Sort(Direction.ASC, "date");
	private static Sort SORT_DESC_DATE = new Sort(Direction.DESC, "date");

	private final HistoricalRateRepository repo;

	private final LatestRateRepository latestRepo;

	private final SymbolService symbolService;

	private final EventBus bus;

	@Autowired
	public RateService(HistoricalRateRepository repo, LatestRateRepository latestRepo, SymbolService symbolService, EventBus bus) {
		this.repo = repo;
		this.latestRepo = latestRepo;
		this.symbolService = symbolService;
		this.bus = bus;
	}

	public Date getOldestRateDate(String fromcur, String tocur) {
		HistoricalRate r = repo.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_ASC_DATE);
		return (r == null) ? null : r.getDate();
	}

	public Date getNewestRateDate(String fromcur, String tocur) {
		HistoricalRate r = repo.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_DESC_DATE);
		return (r == null) ? null : r.getDate();
	}

	public Quote getLatest(String fromcur, String tocur) {
		LatestRate lr = latestRepo.findByFromcurCodeAndTocurCode(fromcur, tocur);

		if (lr != null) {
			return convertToQuote(lr);
		}

		LatestRate lrBaseCurrency = latestRepo.findByFromcurCodeAndTocurCode(BASE_CURRENCY, tocur);

		if (BASE_CURRENCY.equals(fromcur)) {
			return convertToQuote(lrBaseCurrency);
		}

		LatestRate lrAssetCurrency = latestRepo.findByFromcurCodeAndTocurCode(BASE_CURRENCY, fromcur);

		final BigDecimal rate;
		final SymbolEntity symbolFromcur;
		final SymbolEntity symbolTocur;

		Date rateDate = lrAssetCurrency.getDate();

		if (BASE_CURRENCY.equals(tocur)) {
			rate = MathUtils.invert(lrAssetCurrency.getValue());
			symbolFromcur = lrAssetCurrency.getTocur();
			symbolTocur = lrAssetCurrency.getFromcur();
		} else {
			rate = MathUtils.divide(lrBaseCurrency.getValue(), lrAssetCurrency.getValue());

			if (rateDate.before(lrBaseCurrency.getDate())) {
				rateDate = lrBaseCurrency.getDate();
			}

			symbolFromcur = lrAssetCurrency.getTocur();
			symbolTocur = lrBaseCurrency.getTocur();
		}

		return convertToQuote(new RateEntity(symbolFromcur, symbolTocur, rate, rateDate));
	}

	private static Quote convertToQuote(RateEntity re) {
		Quote q = new Quote(re.getFromcur().getCode(), re.getTocur().getCode(), re.getValue(), re.getDate());
		return q;
	}

	private void checkDates(Date start, Date end) {
		Objects.requireNonNull(start, "start is null");
		Objects.requireNonNull(end, "end is null");

		if (start.after(end)) {
			throw new IllegalArgumentException("start (" + start + ") is after end (" + end + ")");
		}
	}

	public List<Object[]> getValues(String fromcur, String tocur, Date start, Date end) {
		checkDates(start, end);
		return repo.findDateValues(fromcur, tocur, start, end);
	}

	public List<Object[]> getHourlyValues(String fromcur, String tocur, Date start, Date end) {
		checkDates(start, end);
		return repo.findHourlyValues(fromcur, tocur, start, end);
	}

	public List<Object[]> getDailyValues(String fromcur, String tocur, Date start, Date end) {
		checkDates(start, end);
		return repo.findDailyValues(fromcur, tocur, start, end);
	}

	public List<Object[]> getWeeklyValues(String fromcur, String tocur, Date start, Date end) {
		checkDates(start, end);
		return repo.findWeeklyValues(fromcur, tocur, start, end);
	}

	public void updateRates(List<Quote> quotes) {

		if (quotes == null || quotes.isEmpty()) {
			log.info("{}: No rates to update");
			return;
		}

		Collections.sort(quotes, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
		});

		List<LatestRate> existingLatestRates = latestRepo.findAll();

		List<HistoricalRate> newHistoRates = new ArrayList<>();
		List<LatestRate> newLatestRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolService.getAll();

		for (Quote quote : quotes) {
			LatestRate existingLatestRate = getFromList(existingLatestRates, quote);

			if (existingLatestRate == null) {
				// optimize
				addCurrency(symbols, quote.getCode());
				addCurrency(symbols, quote.getCurrency());

				LatestRate newLatestRate = new LatestRate(convertToRateEntity(quote));
				newLatestRates.add(newLatestRate);
				newHistoRates.add(new HistoricalRate(convertToRateEntity(quote)));
				existingLatestRates.add(newLatestRate);

			} else if (quote.getTime().after(existingLatestRate.getDate())) {
				existingLatestRate.setDate(quote.getTime());
				existingLatestRate.setValue(quote.getPrice());

				if (!newLatestRates.contains(existingLatestRate)) {
					newLatestRates.add(existingLatestRate);
				}

				newHistoRates.add(new HistoricalRate(convertToRateEntity(quote)));
			}
		}

		latestRepo.save(newLatestRates);
		repo.save(newHistoRates);

		if (!newLatestRates.isEmpty()) {
			bus.post(new RatesUpdatedEvent(newLatestRates));
		}
	}

	@Async
	public void addHistoricalRates(HistoricalRateProvider provider) throws IOException {
		Stopwatch startWatch = Stopwatch.createStarted();

		log.info("{}: Adding historical rates", provider);

		List<Quote> quotes = provider.getHistoricalRates();

		Stopwatch saveWatch = Stopwatch.createStarted();

		addHistoricalRates(quotes);

		log.debug("{}: Data saved in {}", provider, saveWatch);
		log.info("{}: Historical rates added in {}", provider, startWatch);
	}

	public void addHistoricalRates(List<Quote> quotes) {
		Table<String, String, List<Quote>> sortedQuotes = sortQuotes(quotes);

		List<HistoricalRate> newHistoRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolService.getAll();

		for (Cell<String, String, List<Quote>> e : sortedQuotes.cellSet()) {

			List<HistoricalRate> histoRates = repo.findAllByFromcurCodeAndTocurCode(e.getRowKey(), e.getColumnKey());

			for (Quote quote : e.getValue()) {
				addCurrency(symbols, quote.getCode());
				addCurrency(symbols, quote.getCurrency());

				if (!exists(histoRates, quote)) {
					newHistoRates.add(new HistoricalRate(convertToRateEntity(quote)));
				}
			}
		}

		repo.save(newHistoRates);
	}

	public void addLatestRate(Quote quote) {
		RateEntity latestRate = convertToRateEntity(quote);

		latestRepo.save(new LatestRate(latestRate));
	}

	private static RateEntity convertToRateEntity(Quote q) {
		return new RateEntity(q.getCode(), q.getCurrency(), q.getPrice(), q.getTime());
	}

	private void addCurrency(List<SymbolEntity> symbols, String code) {
		for (SymbolEntity se : symbols) {
			if (se.getCode().equals(code)) {
				return;
			}
		}

		CurrencyInfo ci = symbolService.getCurrencyInfo(code);
		symbols.add(symbolService.addSymbol(ci.getCode(), ci.getName(), ci.getCountryFlag(), null));
	}

	private boolean exists(List<HistoricalRate> histoRates, Quote q) {
		for (HistoricalRate hr : histoRates) {
			if (hr.getFromcur().getCode().equals(q.getCode())
					&& hr.getTocur().getCode().equals(q.getCurrency())
					&& hr.getDate().compareTo(q.getTime()) == 0) {
				return true;
			}
		}
		return false;
	}

	private Table<String, String, List<Quote>> sortQuotes(List<Quote> quotes) {
		Table<String, String, List<Quote>> sortedQuotes = HashBasedTable.create();

		for (Quote q : quotes) {

			List<Quote> res = sortedQuotes.get(q.getCode(), q.getCurrency());

			if (res == null) {
				res = new ArrayList<>();
				sortedQuotes.put(q.getCode(), q.getCurrency(), res);
			}

			res.add(q);
		}

		return sortedQuotes;
	}

	private <T extends RateEntity> T getFromList(List<T> existingLatestRates, Quote q) {
		for (T r : existingLatestRates) {
			if (r.getFromcur().getCode().equals(q.getCode())
					&& r.getTocur().getCode().equals(q.getCurrency())) {
				return r;
			}
		}

		return null;
	}

	/**
	 * Removes all latest/historical rates where its fromcur or tocur matches the
	 * given code
	 * 
	 * @param code
	 */
	public void removeAll(String code) {
		latestRepo.deleteByFromcurCodeOrTocurCode(code);
		repo.deleteByFromcurCodeOrTocurCode(code);
	}
}