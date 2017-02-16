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

import static fr.ymanvieu.trading.rate.entity.QHistoricalRate.historicalRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;

import fr.ymanvieu.trading.rate.entity.HistoricalRate;
import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.entity.QLatestRate;
import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;
import fr.ymanvieu.trading.util.DateUtils;
import fr.ymanvieu.trading.util.MathUtils;

@Service
@Transactional(readOnly = true)
public class RateService {

	private static final String BASE_CURRENCY = CurrencyUtils.USD;

	private static final Sort SORT_ASC_DATE = new Sort(Direction.ASC, "date");
	private static final Sort SORT_DESC_DATE = new Sort(Direction.DESC, "date");

	private static final Order FROMCUR_ORDER = new Order(Direction.ASC, "fromcur");
	private static final Order TOCUR_ORDER = new Order(Direction.ASC, "tocur");

	private Logger log = LoggerFactory.getLogger(getClass());

	@Value("${countperpage:10}")
	private int countPerPage;

	@Autowired
	private HistoricalRateRepository histoRepo;

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private SymbolRepository symbolRepo;

	public Date getOldestRateDate(String fromcur, String tocur) {
		HistoricalRate r = histoRepo.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_ASC_DATE);
		return (r == null) ? null : r.getDate();
	}

	public Date getNewestRateDate(String fromcur, String tocur) {
		HistoricalRate r = histoRepo.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_DESC_DATE);
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

	public Page<LatestRate> getLatest(String fromcur, String tocur, Date date, Integer pageNumber, String sortDir,
			String sortedBy) {

		QLatestRate rate = QLatestRate.latestRate;
		BooleanBuilder builder = new BooleanBuilder();

		if (fromcur != null) {
			builder.and(
					rate.fromcur.code.containsIgnoreCase(fromcur).or(rate.fromcur.name.containsIgnoreCase(fromcur)));
		}
		if (tocur != null) {
			builder.and(rate.tocur.code.containsIgnoreCase(tocur).or(rate.tocur.name.containsIgnoreCase(tocur)));
		}
		if (date != null) {
			Date nextDay = DateUtils.nextDay(date);
			builder.and(rate.date.after(date).and(rate.date.before(nextDay)));
		}

		Pageable pageRequest = getPageRequest(pageNumber, sortDir, sortedBy);

		return latestRepo.findAll(builder, pageRequest);
	}

	private Pageable getPageRequest(Integer page, String dir, String sortedBy) {
		// Sort direction
		Direction sortDir = Direction.DESC;
		if ("asc".equalsIgnoreCase(dir)) {
			sortDir = Direction.ASC;
		}
		// Sort fields
		String field = "date";

		if (!StringUtils.isEmpty(sortedBy)) {
			field = sortedBy;
		}

		Order firstOrder = new Order(sortDir, field);

		List<Order> orders = Arrays.asList(firstOrder, FROMCUR_ORDER, TOCUR_ORDER);

		return new PageRequest(page != null && page > 0 ? page - 1 : 0, countPerPage, new Sort(orders));
	}

	/**
	 * Add the given quote. If an existing quote has a time <= to the given
	 * quote, does nothing.
	 * 
	 * @param quote
	 */
	public void addLatestRate(Quote quote) {
		Objects.requireNonNull(quote, "quote must be not null");

		LatestRate existingRate = latestRepo.findByFromcurCodeAndTocurCode(quote.getCode(), quote.getCurrency());

		if (existingRate == null || existingRate.getDate().before(quote.getTime())) {
			RateEntity latestRate = new RateEntity(quote.getCode(), quote.getCurrency(), quote.getPrice(), quote.getTime());
			latestRepo.save(new LatestRate(latestRate));
		}
	}

	private static Quote convertToQuote(RateEntity re) {
		return new Quote(re.getFromcur().getCode(), re.getTocur().getCode(), re.getValue(), re.getDate());
	}

	public List<DateValue> getHistoricalValues(String fromcur, String tocur, Date startDate, Date endDate) {

		Objects.requireNonNull(startDate, "startDate is null");
		Objects.requireNonNull(endDate, "endDate is null");

		Preconditions.checkArgument(!startDate.after(endDate), "startDate:%s is after endDate:%s", startDate, endDate);

		final List<DateValue> result;

		switch (AverageRangeType.getRange(startDate, endDate)) {
			case WEEK:
				result = histoRepo.findWeeklyValues(fromcur, tocur, startDate, endDate);
			break;
			case DAY:
				result = histoRepo.findDailyValues(fromcur, tocur, startDate, endDate);
			break;
			case HOUR:
				result = histoRepo.findHourlyValues(fromcur, tocur, startDate, endDate);
			break;
			case NONE:
			default:
				result = histoRepo.findDateValues(fromcur, tocur, startDate, endDate);
		}

		return result;
	}

	@Transactional
	public void addHistoricalRates(List<Quote> quotes) {

		List<HistoricalRate> ratesToAdd = new ArrayList<>();

		for (Quote quote : quotes) {

			BooleanExpression exp = historicalRate.fromcur.code.eq(quote.getCode())
					.and(historicalRate.tocur.code.eq(quote.getCurrency()))
					.and(historicalRate.date.eq(quote.getTime()));

			if (!histoRepo.exists(exp)) {
				SymbolEntity symbolCode = symbolRepo.findOne(quote.getCode());
				SymbolEntity symbolCurrency = symbolRepo.findOne(quote.getCurrency());

				ratesToAdd.add(new HistoricalRate(new RateEntity(symbolCode, symbolCurrency, quote.getPrice(), quote.getTime())));
			}
		}

		histoRepo.save(ratesToAdd);

		log.debug("{} saved", ratesToAdd.size());
	}
}