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
package fr.ymanvieu.forex.core.web;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysema.query.BooleanBuilder;

import fr.ymanvieu.forex.core.model.entity.rate.LatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.QLatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.model.repositories.LatestRateRepository;
import fr.ymanvieu.forex.core.model.repositories.SymbolRepository;
import fr.ymanvieu.forex.core.util.CurrencyUtils;
import fr.ymanvieu.forex.core.util.DateUtils;

@ConditionalOnMissingBean(RateControllerDev.class)
@RestController
@RequestMapping("/rate")
public class RateController {

	protected enum AVG_VALUES_RANGE {
		NONE, HOUR, DAY, WEEK
	}

	private static final long A_DAY_IN_MS = 24 * 3600 * 1000L;
	private static final long A_WEEK_IN_MS = 7 * A_DAY_IN_MS;

	private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	@Value("${countperpage:10}")
	private int countPerPage;

	@Autowired
	private HistoricalRateRepository repo;

	@Autowired
	private LatestRateRepository latestrepo;

	@Autowired
	private SymbolRepository symbolRepo;

	@RequestMapping("/latest")
	public Page<LatestRate> findLatestByCriteria(String fromcur, String tocur, @DateTimeFormat(pattern = DATE_PATTERN) Date date, Integer pageNumber, String sortDir,
			String sortedBy) {
		Pageable pageRequest = getPageRequest(pageNumber, sortDir, sortedBy);

		QLatestRate rate = QLatestRate.latestRate;
		BooleanBuilder builder = new BooleanBuilder();

		if (fromcur != null) {
			builder.and(rate.fromcur.startsWithIgnoreCase(fromcur));
		}
		if (tocur != null) {
			builder.and(rate.tocur.startsWithIgnoreCase(tocur));
		}
		if (date != null) {
			Date utcDate = DateUtils.toUTC(date);
			Date nextDay = DateUtils.nextDay(utcDate);
			builder.and(rate.date.after(utcDate).and(rate.date.before(nextDay)));
		}

		Page<LatestRate> page = latestrepo.findAll(builder, pageRequest);

		addCurrenciesInfo(page);

		return page;
	}

	@RequestMapping("/raw")
	public List<Object[]> findRawValues(String fromcur, String tocur, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") Date startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") Date endDate) {

		if (fromcur == null || tocur == null) {
			return null;
		}

		final List<Object[]> result;

		switch (getRange(startDate, endDate)) {
		case WEEK:
			result = repo.findWeeklyValues(fromcur, tocur, startDate, endDate);
			break;
		case DAY:
			result = repo.findDailyValues(fromcur, tocur, startDate, endDate);
			break;
		case HOUR:
			result = repo.findHourlyValues(fromcur, tocur, startDate, endDate);
			break;
		case NONE:
		default:
			result = repo.findDateValues(fromcur, tocur, startDate, endDate);
		}

		return result;
	}

	protected AVG_VALUES_RANGE getRange(Date start, Date endDate) {
		if (start == null || endDate == null) {
			return AVG_VALUES_RANGE.WEEK;
		}

		long timeRangeInMs = endDate.getTime() - start.getTime();

		int nbOfMonths = DateUtils.getNbOfMonths(start, endDate);

		if (nbOfMonths >= 24) {
			return AVG_VALUES_RANGE.WEEK;
		}

		if (nbOfMonths >= 6) {
			return AVG_VALUES_RANGE.DAY;
		}

		if (timeRangeInMs >= A_WEEK_IN_MS) {
			return AVG_VALUES_RANGE.HOUR;
		}

		return AVG_VALUES_RANGE.NONE;

	}

	private void addCurrenciesInfo(Page<? extends RateEntity> page) {
		List<SymbolEntity> symbols = symbolRepo.findAll();

		for (RateEntity lr : page) {
			lr.setCountryCodeFrom(CurrencyUtils.codeForCurrency(lr.getFromcur()));

			if (CurrencyUtils.isValidCode(lr.getFromcur())) {
				String fromName = Currency.getInstance(lr.getFromcur()).getDisplayName(Locale.ENGLISH);
				lr.setFromName(fromName);
			} else {
				lr.setFromName(getNameFromSymbols(lr.getFromcur(), symbols));
			}

			lr.setCountryCodeTo(CurrencyUtils.codeForCurrency(lr.getTocur()));

			if (CurrencyUtils.isValidCode(lr.getTocur())) {
				String toName = Currency.getInstance(lr.getTocur()).getDisplayName(Locale.ENGLISH);
				lr.setToName(toName);
			} else {
				lr.setToName(getNameFromSymbols(lr.getTocur(), symbols));
			}
		}
	}

	private String getNameFromSymbols(String code, List<SymbolEntity> symbols) {
		for (SymbolEntity se : symbols) {
			if (code.equals(se.getCode())) {
				return se.getName();
			}
		}

		return null;
	}

	private Pageable getPageRequest(Integer page, String dir, String sortedBy) {
		// Sort direction
		Direction sortDir = Direction.DESC;
		if ("asc".equalsIgnoreCase(dir)) {
			sortDir = Direction.ASC;
		}
		// Sort fields
		String[] fields = new String[1];
		if (!StringUtils.isEmpty(sortedBy)) {
			fields[0] = sortedBy;
		} else {
			fields[0] = "date";
		}
		return new PageRequest(page != null && page > 0 ? page - 1 : 0, countPerPage, sortDir, fields);
	}
}