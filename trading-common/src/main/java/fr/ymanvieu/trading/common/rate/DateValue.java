package fr.ymanvieu.trading.common.rate;

import java.time.Instant;

public interface DateValue {

	Instant getDate();

	Double getValue();
}
