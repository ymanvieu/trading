package fr.ymanvieu.trading.common.rate;

import static fr.ymanvieu.trading.common.rate.entity.QHistoricalRate.historicalRate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Preconditions;
import com.querydsl.core.types.dsl.BooleanExpression;

import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.rate.entity.HistoricalRate;
import fr.ymanvieu.trading.common.rate.entity.LatestRate;
import fr.ymanvieu.trading.common.rate.mapper.RateMapper;
import fr.ymanvieu.trading.common.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.common.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;
import fr.ymanvieu.trading.common.symbol.Currency;
import fr.ymanvieu.trading.common.util.MathUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class RateService {

	private static final String BASE_CURRENCY = Currency.USD;

	private static final Sort SORT_ASC_DATE = Sort.by(Direction.ASC, "date");
	private static final Sort SORT_DESC_DATE = Sort.by(Direction.DESC, "date");

	@Autowired
	private HistoricalRateRepository historicalRateRepository;

	@Autowired
	private LatestRateRepository latestRateRepository;

	@Autowired
	private SymbolRepository symbolRepository;
	
	@Autowired
	private RateMapper rateMapper;
	
	public List<FavoriteRate> getAllLatestWithFavorites(Integer userId) {
		return latestRateRepository.findAllWithFavorites(userId);
	}

	public Instant getOldestRateDate(String fromcur, String tocur) {
		HistoricalRate r = historicalRateRepository.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_ASC_DATE);
		return (r == null) ? null : r.getDate();
	}

	public Instant getNewestRateDate(String fromcur, String tocur) {
		HistoricalRate r = historicalRateRepository.findFirstByFromcurCodeAndTocurCode(fromcur, tocur, SORT_DESC_DATE);
		return (r == null) ? null : r.getDate();
	}
	
	public Rate getLatest(String fromcur, String tocur) {
		
		LatestRate lr = latestRateRepository.findByFromcurCodeAndTocurCode(fromcur, tocur);

		if (lr != null) {
			return rateMapper.mapToRate(lr);
		}

		LatestRate lrBaseCurrency = latestRateRepository.findByFromcurCodeAndTocurCode(BASE_CURRENCY, tocur);

		LatestRate lrBaseCurrencyFromcur = latestRateRepository.findByFromcurCodeAndTocurCode(BASE_CURRENCY, fromcur);

		final BigDecimal rate;
		final SymbolEntity symbolFromcur;
		final SymbolEntity symbolTocur;

		if (BASE_CURRENCY.equals(tocur) && lrBaseCurrencyFromcur == null) {
			return null;
		}

		Instant rateDate = lrBaseCurrencyFromcur.getDate();

		if (BASE_CURRENCY.equals(tocur)) {
			rate = MathUtils.invert(lrBaseCurrencyFromcur.getValue());
			symbolFromcur = lrBaseCurrencyFromcur.getTocur();
			symbolTocur = lrBaseCurrencyFromcur.getFromcur();
		} else {
			rate = MathUtils.divide(lrBaseCurrency.getValue(), lrBaseCurrencyFromcur.getValue());

			if (rateDate.isBefore(lrBaseCurrency.getDate())) {
				rateDate = lrBaseCurrency.getDate();
			}

			symbolFromcur = lrBaseCurrencyFromcur.getTocur();
			symbolTocur = lrBaseCurrency.getTocur();
		}

		return rateMapper.mapToRate(new LatestRate(symbolFromcur, symbolTocur, rate, rateDate));
	}

	/**
	 * Add the given quote. If an existing quote has a time > to the given quote, does nothing.
	 */
	public void addLatestRate(Quote quote) {
		Objects.requireNonNull(quote, "quote must be not null");

		LatestRate existingRate = latestRateRepository.findByFromcurCodeAndTocurCode(quote.code(), quote.currency());

		if (existingRate == null) {
			var sourceSymbol = symbolRepository.findById(quote.code()).orElseThrow();
			var currencySymbol = symbolRepository.findById(quote.currency()).orElseThrow();

			latestRateRepository.save(new LatestRate(sourceSymbol, currencySymbol, quote.price(), quote.time()));
		} else if (existingRate.getDate().isBefore(quote.time())) {
			existingRate.setDate(quote.time());
			existingRate.setValue(quote.price());
			latestRateRepository.save(existingRate);
		}
	}

	public List<DateValue> getHistoricalValues(String fromcur, String tocur, Instant startDate, Instant endDate) {

		Objects.requireNonNull(startDate, "startDate is null");
		Objects.requireNonNull(endDate, "endDate is null");

		Preconditions.checkArgument(!startDate.isAfter(endDate), "startDate:%s is after endDate:%s", startDate, endDate);

		return switch (AverageRangeType.getRange(startDate, endDate)) {
			case WEEK -> historicalRateRepository.findWeeklyValues(fromcur, tocur, startDate, endDate);
			case DAY -> historicalRateRepository.findDailyValues(fromcur, tocur, startDate, endDate);
			case HOUR -> historicalRateRepository.findHourlyValues(fromcur, tocur, startDate, endDate);
			default -> historicalRateRepository.findDateValues(fromcur, tocur, startDate, endDate);
		};
	}

	public void addHistoricalRates(List<Quote> quotes) {

		List<HistoricalRate> ratesToAdd = new ArrayList<>();

		for (Quote quote : quotes) {

			BooleanExpression exp = historicalRate.fromcur.code.eq(quote.code())
					.and(historicalRate.tocur.code.eq(quote.currency()))
					.and(historicalRate.date.eq(quote.time()));

			if (!historicalRateRepository.exists(exp)) {
				SymbolEntity symbolCode = symbolRepository.findById(quote.code()).orElseThrow();
				SymbolEntity symbolCurrency = symbolRepository.findById(quote.currency()).orElseThrow();

				ratesToAdd.add(new HistoricalRate(symbolCode, symbolCurrency, quote.price(), quote.time()));
			}
		}

		historicalRateRepository.saveAll(ratesToAdd);

		log.debug("{} saved", ratesToAdd.size());
	}

	public void deleteRates(String symbolCode, String currencyCode) {
		historicalRateRepository.deleteAllByFromcurCodeAndTocurCode(symbolCode, currencyCode);
		latestRateRepository.deleteByFromcurCodeAndTocurCode(symbolCode, currencyCode);
	}

	public void updateRates(Pair oldPair, Symbol newSymbol) {
		latestRateRepository.update(oldPair, newSymbol);
		historicalRateRepository.updateAll(oldPair, newSymbol);
	}
}
