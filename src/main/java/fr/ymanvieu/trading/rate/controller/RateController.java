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
package fr.ymanvieu.trading.rate.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.expr.BooleanExpression;

import fr.ymanvieu.trading.rate.entity.QLatestRate;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.util.DateUtils;

@RestController
@RequestMapping("/rate")
public class RateController {

	protected enum AVG_VALUES_RANGE {
		NONE,
		HOUR,
		DAY,
		WEEK
	}

	private static final long A_WEEK_IN_MS = 7 * 24 * 3600 * 1000L;

	private static final String CRITERIA_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final String RAW_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private static final Order FROMCUR_ORDER = new Order(Direction.ASC, "fromcur");
	private static final Order TOCUR_ORDER = new Order(Direction.ASC, "tocur");

	@Value("${countperpage:10}")
	private int countPerPage;

	@Autowired
	private HistoricalRateRepository repo;

	@Autowired
	private LatestRateRepository latestrepo;

	@Autowired
	private RateService rateService;

	@RequestMapping("/latest")
	public Page<LatestRate> findLatestByCriteria(String fromcur, String tocur, @DateTimeFormat(pattern = CRITERIA_DATE_PATTERN) Date date,
			Integer pageNumber, String sortDir, String sortedBy) {

		QLatestRate rate = QLatestRate.latestRate;
		BooleanBuilder builder = new BooleanBuilder();

		if (fromcur != null) {
			BooleanExpression fromcurExpression = rate.fromcur.code.containsIgnoreCase(fromcur);
			fromcurExpression = fromcurExpression.or(rate.fromcur.name.containsIgnoreCase(fromcur));
			builder.and(fromcurExpression);
		}
		if (tocur != null) {
			BooleanExpression tocurExpression = rate.tocur.code.containsIgnoreCase(tocur);
			tocurExpression = tocurExpression.or(rate.tocur.name.containsIgnoreCase(tocur));
			builder.and(tocurExpression);
		}
		if (date != null) {
			Date nextDay = DateUtils.nextDay(date);
			builder.and(rate.date.after(date).and(rate.date.before(nextDay)));
		}

		Pageable pageRequest = getPageRequest(pageNumber, sortDir, sortedBy);

		Page<LatestRate> page = latestrepo.findAll(builder, pageRequest);

		return page;
	}

	@RequestMapping("/raw")
	public List<Object[]> findRawValues(String fromcur, String tocur, @DateTimeFormat(pattern = RAW_DATE_PATTERN) Date startDate,
			@DateTimeFormat(pattern = RAW_DATE_PATTERN) Date endDate) {

		if (fromcur == null || tocur == null) {
			return null;
		}

		if (startDate == null) {
			startDate = rateService.getOldestRateDate(fromcur, tocur);
		}

		if (endDate == null) {
			endDate = rateService.getNewestRateDate(fromcur, tocur);
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

}