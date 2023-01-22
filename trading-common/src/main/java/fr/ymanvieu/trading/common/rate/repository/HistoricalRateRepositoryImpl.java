package fr.ymanvieu.trading.common.rate.repository;

import static com.querydsl.core.types.Projections.constructor;
import static fr.ymanvieu.trading.common.rate.entity.QHistoricalRate.historicalRate;

import java.time.Instant;
import java.util.List;

import com.querydsl.jpa.impl.JPAQuery;

import fr.ymanvieu.trading.common.rate.AverageRangeType;
import fr.ymanvieu.trading.common.rate.DateValue;
import fr.ymanvieu.trading.common.rate.DateValueImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
