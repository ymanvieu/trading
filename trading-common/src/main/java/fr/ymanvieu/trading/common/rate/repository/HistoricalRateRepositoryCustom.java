package fr.ymanvieu.trading.common.rate.repository;

import java.time.Instant;
import java.util.List;

import fr.ymanvieu.trading.common.rate.DateValue;

public interface HistoricalRateRepositoryCustom {

	List<DateValue> findValues(String fromcur, String tocur, Instant startDate, Instant endDate);
}
