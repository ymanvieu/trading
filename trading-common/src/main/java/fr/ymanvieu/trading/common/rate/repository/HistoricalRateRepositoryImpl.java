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
package fr.ymanvieu.trading.common.rate.repository;

import static com.querydsl.core.types.Projections.constructor;
import static fr.ymanvieu.trading.common.rate.entity.QHistoricalRate.historicalRate;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQuery;

import fr.ymanvieu.trading.common.rate.AverageRangeType;
import fr.ymanvieu.trading.common.rate.DateValue;
import fr.ymanvieu.trading.common.rate.DateValueImpl;

public class HistoricalRateRepositoryImpl implements HistoricalRateRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<DateValue> findValues(String fromcur, String tocur, Instant startDate, Instant endDate) {

		AverageRangeType type = AverageRangeType.getRange(startDate, endDate);

		JPAQuery<DateValue> q = new JPAQuery<>(em);

		if (type == AverageRangeType.NONE) {
			q.select(constructor(DateValueImpl.class, historicalRate.date, historicalRate.value.doubleValue()));
			q.orderBy(historicalRate.date.asc());
		} else {
			q.select(constructor(DateValueImpl.class, historicalRate.date.min(), historicalRate.value.avg()));
			q.orderBy(historicalRate.date.min().asc());
		}

		q.from(historicalRate) //
				.where(historicalRate.fromcur.code.eq(fromcur) //
						.and(historicalRate.tocur.code.eq(tocur)) //
						.and(historicalRate.date.between(startDate, endDate)));

		switch (type) {
			case HOUR:
				q.groupBy(historicalRate.date.year(), historicalRate.date.dayOfYear(), historicalRate.date.hour());
			break;
			case DAY:
				q.groupBy(historicalRate.date.year(), historicalRate.date.dayOfYear());
			break;
			case WEEK:
				q.groupBy(historicalRate.date.year(), historicalRate.date.week());
			break;
			case NONE:
			default:
		}

		return q.fetch();
	}

}
